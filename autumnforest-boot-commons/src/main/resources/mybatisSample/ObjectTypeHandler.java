package top.qiulin.dev.task.enums;

import org.apache.ibatis.type.MappedTypes;
import top.autumnforest.commons.web.mybatis.BaseJsonTypeHandler;
import top.qiulin.dev.task.model.DetJson;

@MappedTypes({Xxx.class})
public class ObjectTypeHandler<T> extends BaseJsonTypeHandler<T> {

    public ObjectTypeHandler(Class<T> type) {
        super(type);
    }
}
