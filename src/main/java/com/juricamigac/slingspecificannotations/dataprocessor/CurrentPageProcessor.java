package com.juricamigac.slingspecificannotations.dataprocessor;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import com.juricamigac.slingspecificannotations.annotations.CurrentPage;
import com.juricamigac.slingspecificannotations.constants.ServiceRankingConstants;
import com.juricamigac.slingspecificannotations.dataprocessor.annotationsprocessor.CurrentPageMetadataProviderAnnotationProcessor;
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
public class CurrentPageProcessor implements Injector, StaticInjectAnnotationProcessorFactory {

    private static final String CURRENT_PAGE_SOURCE = "current-page-sling-specific-injection";

    @Reference
    private PageManagerFactory pageManagerFactory;

    @Override
    public String getName() {
        return CURRENT_PAGE_SOURCE;
    }

    @Override
    public Object getValue(Object adaptable, String fieldName, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
        if (adaptable instanceof Resource && this.isPage(type, annotatedElement)) {
            final Resource resource = (Resource) adaptable;
            return this.getPageFromAdaptable(resource);
        } else if (adaptable instanceof SlingHttpServletRequest && this.isPage(type, annotatedElement)) {
            final SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
            return this.getPageFromAdaptable(request.getResource());
        }
        return null;
    }

    private boolean isPage(final Type type, final AnnotatedElement annotatedElement) {
        return type.equals(Page.class) && annotatedElement.isAnnotationPresent(CurrentPage.class);
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement annotatedElement) {
        final CurrentPage annotation = annotatedElement.getAnnotation(CurrentPage.class);
        if (annotation != null) {
            return new CurrentPageMetadataProviderAnnotationProcessor();
        }
        return null;
    }

    private Page getPageFromAdaptable(final Resource resource) {
        final PageManager pageManager = this.pageManagerFactory.getPageManager(resource.getResourceResolver());
        return pageManager.getContainingPage(resource);
    }

}
