package io.github.autumnforest.boot.utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class FreemarkerUtil {

    public static String process(String templateString, Object data) throws Exception {
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setTemplateLoader(new StringTemplateLoader());
        configuration.setNumberFormat("#");
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        Template template = new Template("StringTemplateLoader", templateString, configuration);
        StringWriter out = new StringWriter();
        template.process(data, out);
        return out.toString();
    }
}
