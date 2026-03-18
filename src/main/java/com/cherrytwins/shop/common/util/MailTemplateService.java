package com.cherrytwins.shop.common.util;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class MailTemplateService {

    private final TemplateEngine mailTemplateEngine;

    public MailTemplateService(TemplateEngine mailTemplateEngine) {
        this.mailTemplateEngine = mailTemplateEngine;
    }

    public String render(String templateName, Map<String, Object> model) {
        Context ctx = new Context();
        ctx.setVariables(model);
        return mailTemplateEngine.process(templateName, ctx);
    }
}