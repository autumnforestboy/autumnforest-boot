package io.github.autumnforest.boot.commons.jpa;


import io.github.autumnforest.boot.commons.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public interface BaseController<T> {
    JpaRepository<T, Long> getRepository();

    @GetMapping("get")
    default Result<T> get(@Valid @NotNull @RequestParam Long id) {
        return Result.ok(getRepository().findById(id).get());
    }

    @GetMapping("save")
    default Result<Void> save(@RequestBody T entity) {
        getRepository().save(entity);
        return Result.ok(null);
    }

    @GetMapping("del")
    default Result<Void> del(@Valid @NotNull @RequestParam Long id) {
        getRepository().deleteById(id);
        return Result.ok(null);
    }

    @GetMapping("del")
    default Result<Void> page(@Valid @NotNull @RequestParam Long id) {
//        getRepository().findAll()
        return Result.ok(null);
    }
}
