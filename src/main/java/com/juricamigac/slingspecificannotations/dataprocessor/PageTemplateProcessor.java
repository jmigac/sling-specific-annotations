package com.juricamigac.slingspecificannotations.dataprocessor;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.day.cq.wcm.api.Template;
import com.juricamigac.slingspecificannotations.annotations.PageTemplate;
import com.juricamigac.slingspecificannotations.constants.ServiceRankingConstants;
import com.juricamigac.slingspecificannotations.dataprocessor.annotationsprocessor.PageTemplateMetadataProviderAnnotationProcessor;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import static org.osgi.framework.Constants.SERVICE_RANKING;

@Component(
        immediate = true,
        service = {Injector.class, StaticInjectAnnotationProcessorFactory.class},
        property = {SERVICE_RANKING + ":Integer=" + ServiceRankingConstants.SLING_ANNOTATIONS_SERVICE_RANKING})
public class PageTemplateProcessor implements Injector, StaticInjectAnnotationProcessorFactory {

    private static final String PAGE_TEMPLATE_ANNOTATION_SOURCE = "page-template-sling-specific-injection";

    @Reference
    private PageManagerFactory pageManagerFactory;

    @Override
    public String getName() {
        return PAGE_TEMPLATE_ANNOTATION_SOURCE;
    }

    @Override
    public Object getValue(Object adaptable, String fieldName, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
        if (adaptable instanceof Resource && this.isTemplate(type, annotatedElement)) {
            final Resource resource = (Resource) adaptable;
            return this.getPageTemplateFromResource(resource);
        } else if (adaptable instanceof SlingHttpServletRequest && this.isTemplate(type, annotatedElement)) {
            final SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
            return this.getPageTemplateFromResource(request.getResource());
        }
        return null;
    }

    private boolean isTemplate(final Type type, final AnnotatedElement annotatedElement) {
        return type.equals(Template.class) && annotatedElement.isAnnotationPresent(PageTemplate.class);
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement annotatedElement) {
        final PageTemplate annotation = annotatedElement.getAnnotation(PageTemplate.class);
        if (annotation != null) {
            return new PageTemplateMetadataProviderAnnotationProcessor();
        }
        return null;
    }

    private Template getPageTemplateFromResource(final Resource resource) {
        final PageManager pageManager = this.pageManagerFactory.getPageManager(resource.getResourceResolver());
        final Page page = pageManager.getContainingPage(resource);
        return page.getTemplate();
    }

}
