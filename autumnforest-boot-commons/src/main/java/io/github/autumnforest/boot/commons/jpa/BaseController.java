package io.github.autumnforest.boot.commons.jpa;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.autumnforest.boot.result.PageParam;
import io.github.autumnforest.boot.result.PageResult;
import io.github.autumnforest.boot.result.Result;
import io.github.autumnforest.boot.utils.DateUtil;
import io.github.autumnforest.boot.utils.SqlUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Validated
public abstract class BaseController<T> {
    Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected TableSchema tableSchema = new TableSchema();
    private JpaRepository<T, Long> repository;
    private JpaSpecificationExecutor<T> jpaSpecificationExecutor;
    @Autowired
    ObjectMapper objectMapper;

    protected BaseController(JpaRepository<T, Long> repository, JpaSpecificationExecutor<T> jpaSpecificationExecutor) {
        this.jpaSpecificationExecutor = jpaSpecificationExecutor;
        this.repository = repository;
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Class<T> classOfT = (Class) params[0];
        Class<?> clazz = classOfT;
        //遍历实体和所有父类中的所有字段，排除非表字段，得到所有表字段
        while (clazz != null) {
            Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) {
                    return;
                }
                if (Modifier.isFinal(field.getModifiers())) {
                    return;
                }
                if (field.getAnnotation(Transient.class) != null) {
                    return;
                }
                if (field.getAnnotation(JoinColumn.class) != null) {
                    return;
                }
                if (field.getAnnotation(JoinTable.class) != null) {
                    return;
                }
                if (field.getAnnotation(ManyToMany.class) != null) {
                    return;
                }
                if (field.getAnnotation(OneToOne.class) != null) {
                    return;
                }
                if (field.getAnnotation(OneToMany.class) != null) {
                    return;
                }
                if (field.getAnnotation(ManyToOne.class) != null) {
                    return;
                }
                if (field.getAnnotation(PrimaryKeyJoinColumn.class) != null) {
                    return;
                }
                TableColumn tableColumn = new TableColumn();
                Column columnAnnotation = field.getAnnotation(Column.class);
                tableColumn.setColumnType(field.getType());
                tableColumn.setClassColumnName(field.getName());
                if (columnAnnotation != null) {
                    tableColumn.setTableColumnName(columnAnnotation.name());
                } else {
                    tableColumn.setTableColumnName(SqlUtil.humpToLine2(field.getName()));
                }
                tableColumn.setFiled(field);
                tableSchema.addColumn(tableColumn);
            });
            clazz = clazz.getSuperclass();
        }
    }


    @GetMapping("findById")
    public Result<T> findById(@RequestParam @NotNull Long id) {
        return Result.ok(repository.findById(id).get());
    }

    @PostMapping("save")
    public Result<T> save(@RequestBody @NotNull T entity) {
        repository.save(entity);
        return Result.ok(entity);
    }

    @DeleteMapping("deleteById")
    public Result<Void> deleteById(@RequestParam @NotNull Long id) {
        repository.deleteById(id);
        return Result.ok(null);
    }


    @PostMapping("page")
    public Result<PageResult<T>> page(@RequestBody PageParam<QueryInfo<T>> pageParam) throws Exception {
        return Result.ok(doPage(pageParam));
    }

    private PageResult<T> doPage(@RequestBody PageParam<QueryInfo<T>> pageParam) throws Exception {
        final Integer selectPage = pageParam.getCurrentPage();
        QueryInfo<T> queryInfo = pageParam.getQueryInfo();
        if (selectPage < 1 && selectPage != -1) {
            throw new Exception("页码currentPage必须是正整数或-1（表示不分页）");
        }
        int pageIndex = selectPage - 1;
        if (selectPage == -1) {
            List<T> all = list(queryInfo);
            return new PageResult<>(1, all.size(), 1, all);
        }
        //构建通用查询条件
        Specification<T> spec = getSpecification(queryInfo);
        Page<T> page = null;
        List<QueryInfo.Sort> sortFields = queryInfo.getSortFields();
        //是否进行排序
        if (CollectionUtils.isNotEmpty(sortFields)) {
            ArrayList<Sort.Order> sorts = new ArrayList<>();
            for (QueryInfo.Sort sort : sortFields) {
                Sort.Direction direction = sort.getAscend() == null || sort.getAscend() ? Sort.Direction.ASC : Sort.Direction.DESC;
                sorts.add(new Sort.Order(direction, sort.getField()));
            }
            page = jpaSpecificationExecutor.findAll(spec, PageRequest.of(pageIndex, pageParam.getPageSize(), Sort.by(sorts)));
        } else {
            page = jpaSpecificationExecutor.findAll(spec, PageRequest.of(pageIndex, pageParam.getPageSize()));
        }
        return new PageResult<>(page.getTotalPages(), page.getTotalElements(), page.getPageable().getPageNumber() + 1, page.getContent());
    }

    private List<T> list(QueryInfo<T> pageParam) throws Exception {
        Specification<T> spec = getSpecification(pageParam);
        List<T> list = null;
        List<QueryInfo.Sort> sortFields = pageParam.getSortFields();
        //是否进行排序

        if (CollectionUtils.isNotEmpty(sortFields)) {
            ArrayList<Sort.Order> sorts = new ArrayList<>();
            for (QueryInfo.Sort sort : sortFields) {
                Sort.Direction direction = sort.getAscend() == null || sort.getAscend() ? Sort.Direction.ASC : Sort.Direction.DESC;
                sorts.add(new Sort.Order(direction, sort.getField()));
            }
            list = jpaSpecificationExecutor.findAll(spec, Sort.by(sorts));
        } else {
            list = jpaSpecificationExecutor.findAll(spec);
        }
        return list;
    }


    private Specification<T> getSpecification(QueryInfo<T> param) throws Exception {
        logger.debug("param:{}", param);
        Map<String, Object> values;
        if (param.getSearch() == null) {
            values = new HashMap<>();
        } else {
            String search = objectMapper.writeValueAsString(param.getSearch());
            values = objectMapper.readValue(search, Map.class);
            values.entrySet().removeIf(entry -> entry.getValue() == null);
        }
//        Map<String, Object> values = PropertyUtils.describe(param.getSearch());

        List<String> nullAble = param.getNullAble() == null ? new ArrayList<>() : param.getNullAble();
        List<String> fuzzy = param.getFuzzy() == null ? new ArrayList<>() : param.getFuzzy();

        List<TableColumn> columns = new LinkedList<>();
        List<QueryInfo.Range> ranges = param.getRanges();
        tableSchema.check(ranges);
        List<QueryInfo.Range> sortRanges = new LinkedList<>();

        for (TableColumn c : tableSchema.getColumns()) {
            TableColumn column = new TableColumn(c);
            column.setColumnValue(values.get(c.getClassColumnName()));
            columns.add(column);

            if (ranges != null && ranges.size() > 0) {
                for (QueryInfo.Range range : ranges) {
                    if (range.getFiled().equals(c.getClassColumnName())) {
                        sortRanges.add(range);
                        break;
                    }
                }
            }
        }

        return (root, query, cb) -> {
            //条件构建
            List<Predicate> predicates = new ArrayList<>();
            //=,like条件
            for (TableColumn column : columns) {
                //非空值或指定空值字段加入查询条件
                if (column.getColumnValue() != null || nullAble.contains(column.getClassColumnName())) {
                    Predicate predicate;
                    //字符串类型且指定模糊查询的进行模糊查询
                    if (String.class.isAssignableFrom(column.getColumnType()) && fuzzy.contains(column.getClassColumnName())) {
                        predicate = cb.like(root.get(column.getClassColumnName()), column.getColumnValue().toString());
                    } else {
                        predicate = cb.equal(root.get(column.getClassColumnName()), column.getColumnValue());
                    }
                    predicates.add(predicate);
                }
            }

            //范围添加
            if (sortRanges.size() > 0) {

                for (QueryInfo.Range range : sortRanges) {
                    String rangeFiled = range.getFiled();
                    logger.debug("rangeFiled:{}", rangeFiled);
                    Class classByName = tableSchema.getClassByName(rangeFiled);
                    logger.debug("class of range:{}", classByName);
                    {
                        QueryInfo.Range.Value startValue = range.getStartValue();
                        logger.debug("startValue:{}", startValue);
                        if (startValue != null && startValue.getValue() != null) {
                            Predicate predicate = null;
                            if (startValue.isEqual()) {
                                if (Date.class.isAssignableFrom(classByName)) {
                                    logger.debug("Date.class.isAssignableFrom");
                                    predicate = cb.greaterThanOrEqualTo(root.get(rangeFiled), DateUtil.str2Date(startValue.getValue().toString()));
                                } else if (LocalDateTime.class.isAssignableFrom(classByName)) {
                                    predicate = cb.greaterThanOrEqualTo(root.get(rangeFiled), LocalDateTime.parse(startValue.getValue().toString(), DateUtil.yyyyMMddHHmmss));
                                } else if (Number.class.isAssignableFrom(classByName)) {
                                    logger.debug("Number.class.isAssignableFrom");
                                    if (Integer.class.isAssignableFrom(classByName)) {
                                        predicate = cb.greaterThanOrEqualTo(root.get(rangeFiled), (Integer) startValue.getValue());
                                    } else if (Long.class.isAssignableFrom(classByName)) {
                                        predicate = cb.greaterThanOrEqualTo(root.get(rangeFiled), (Long) startValue.getValue());
                                    } else if (BigInteger.class.isAssignableFrom(classByName)) {
                                        predicate = cb.greaterThanOrEqualTo(root.get(rangeFiled), (BigInteger) startValue.getValue());
                                    } else if (Double.class.isAssignableFrom(classByName)) {
                                        predicate = cb.greaterThanOrEqualTo(root.get(rangeFiled), (Double) startValue.getValue());
                                    }
                                } else if (Boolean.class.isAssignableFrom(classByName)) {
                                    logger.debug("Boolean.class.isAssignableFrom");
                                    predicate = cb.greaterThanOrEqualTo(root.get(rangeFiled), (Boolean) startValue.getValue());
                                } else {
                                    logger.debug("else.class.isAssignableFrom");
                                    predicate = cb.greaterThanOrEqualTo(root.get(rangeFiled), (String) startValue.getValue());
                                }
                            } else {
                                if (Date.class.isAssignableFrom(classByName)) {
                                    logger.debug("Date.class.isAssignableFrom");
                                    predicate = cb.greaterThan(root.get(rangeFiled), DateUtil.str2Date(startValue.getValue().toString()));
                                } else if (LocalDateTime.class.isAssignableFrom(classByName)) {
                                    predicate = cb.greaterThan(root.get(rangeFiled), LocalDateTime.parse(startValue.getValue().toString(), DateUtil.yyyyMMddHHmmss));
                                } else if (Number.class.isAssignableFrom(classByName)) {
                                    logger.debug("Number.class.isAssignableFrom");
                                    if (Integer.class.isAssignableFrom(classByName)) {
                                        predicate = cb.greaterThan(root.get(rangeFiled), (Integer) startValue.getValue());
                                    } else if (Long.class.isAssignableFrom(classByName)) {
                                        predicate = cb.greaterThan(root.get(rangeFiled), (Long) startValue.getValue());
                                    } else if (BigInteger.class.isAssignableFrom(classByName)) {
                                        predicate = cb.greaterThan(root.get(rangeFiled), (BigInteger) startValue.getValue());
                                    } else if (Double.class.isAssignableFrom(classByName)) {
                                        predicate = cb.greaterThan(root.get(rangeFiled), (Double) startValue.getValue());
                                    }
                                } else if (Boolean.class.isAssignableFrom(classByName)) {
                                    logger.debug("Boolean.class.isAssignableFrom");
                                    predicate = cb.greaterThan(root.get(rangeFiled), (Boolean) startValue.getValue());
                                } else {
                                    logger.debug("else.class.isAssignableFrom");
                                    predicate = cb.greaterThan(root.get(rangeFiled), (String) startValue.getValue());
                                }
                            }
                            predicates.add(predicate);
                        }
                    }
                    {
                        QueryInfo.Range.Value endValue = range.getEndValue();
                        logger.debug("endValue:{}", endValue);
                        if (endValue != null && endValue.getValue() != null) {
                            Predicate predicate = null;
                            if (endValue.isEqual()) {
                                if (Date.class.isAssignableFrom(classByName)) {
                                    logger.debug("Date.class.isAssignableFrom");
                                    predicate = cb.lessThanOrEqualTo(root.get(rangeFiled), DateUtil.str2Date(endValue.getValue().toString()));
                                } else if (LocalDateTime.class.isAssignableFrom(classByName)) {
                                    predicate = cb.lessThanOrEqualTo(root.get(rangeFiled), LocalDateTime.parse(endValue.getValue().toString(), DateUtil.yyyyMMddHHmmss));
                                } else if (Number.class.isAssignableFrom(classByName)) {
                                    logger.debug("Number.class.isAssignableFrom");
                                    if (Integer.class.isAssignableFrom(classByName)) {
                                        predicate = cb.lessThanOrEqualTo(root.get(rangeFiled), (Integer) endValue.getValue());
                                    } else if (Long.class.isAssignableFrom(classByName)) {
                                        predicate = cb.lessThanOrEqualTo(root.get(rangeFiled), (Long) endValue.getValue());
                                    } else if (BigInteger.class.isAssignableFrom(classByName)) {
                                        predicate = cb.lessThanOrEqualTo(root.get(rangeFiled), (BigInteger) endValue.getValue());
                                    } else if (Double.class.isAssignableFrom(classByName)) {
                                        predicate = cb.lessThanOrEqualTo(root.get(rangeFiled), (Double) endValue.getValue());
                                    }
                                } else if (Boolean.class.isAssignableFrom(classByName)) {
                                    logger.debug("Boolean.class.isAssignableFrom");
                                    predicate = cb.lessThanOrEqualTo(root.get(rangeFiled), (Boolean) endValue.getValue());
                                } else {
                                    logger.debug("else.class.isAssignableFrom");
                                    predicate = cb.lessThanOrEqualTo(root.get(rangeFiled), (String) endValue.getValue());
                                }
                            } else {
                                if (Date.class.isAssignableFrom(classByName)) {
                                    logger.debug("Date.class.isAssignableFrom");
                                    predicate = cb.lessThan(root.get(rangeFiled), DateUtil.str2Date(endValue.getValue().toString()));
                                } else if (LocalDateTime.class.isAssignableFrom(classByName)) {
                                    predicate = cb.lessThan(root.get(rangeFiled), LocalDateTime.parse(endValue.getValue().toString(), DateUtil.yyyyMMddHHmmss));
                                } else if (Number.class.isAssignableFrom(classByName)) {
                                    logger.debug("Number.class.isAssignableFrom");
                                    if (Integer.class.isAssignableFrom(classByName)) {
                                        predicate = cb.lessThan(root.get(rangeFiled), (Integer) endValue.getValue());
                                    } else if (Long.class.isAssignableFrom(classByName)) {
                                        predicate = cb.lessThan(root.get(rangeFiled), (Long) endValue.getValue());
                                    } else if (BigInteger.class.isAssignableFrom(classByName)) {
                                        predicate = cb.lessThan(root.get(rangeFiled), (BigInteger) endValue.getValue());
                                    } else if (Double.class.isAssignableFrom(classByName)) {
                                        predicate = cb.lessThan(root.get(rangeFiled), (Double) endValue.getValue());
                                    }
                                } else if (Boolean.class.isAssignableFrom(classByName)) {
                                    logger.debug("Boolean.class.isAssignableFrom");
                                    predicate = cb.lessThan(root.get(rangeFiled), (Boolean) endValue.getValue());
                                } else {
                                    logger.debug("else.class.isAssignableFrom");
                                    predicate = cb.lessThan(root.get(rangeFiled), (String) endValue.getValue());
                                }
                            }
                            predicates.add(predicate);
                        }
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}
