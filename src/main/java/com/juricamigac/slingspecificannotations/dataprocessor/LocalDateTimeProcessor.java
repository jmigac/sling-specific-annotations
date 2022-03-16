package com.juricamigac.slingspecificannotations.dataprocessor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import com.juricamigac.slingspecificannotations.annotations.LocalDateTimeValueMapValue;
import com.juricamigac.slingspecificannotations.dataprocessor.annotationsprocessor.RequestedDateTimeMetadataProviderAnnotationProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component(immediate = true, service = { Injector.class, StaticInjectAnnotationProcessorFactory.class })
@ServiceRanking(LocalDateTimeProcessor.SERVICE_RANKING)
public class LocalDateTimeProcessor implements Injector, StaticInjectAnnotationProcessorFactory {

    protected static final int SERVICE_RANKING = Integer.MAX_VALUE;
    private static final String INJECTION_NAME = "local-date-time-provider-injector";

    @Override
    public String getName() {
        return INJECTION_NAME;
    }

    @Override
    public Object getValue(Object adaptable, String fieldName, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
        if (adaptable instanceof SlingHttpServletRequest && annotatedElement.isAnnotationPresent(LocalDateTimeValueMapValue.class)) {
            return this.getLocalDateTimeFromAdaptableRequest(adaptable, annotatedElement, fieldName);
        } else if (adaptable instanceof Resource && annotatedElement.isAnnotationPresent(LocalDateTimeValueMapValue.class)) {
            return this.getLocalDateTimeFromAdaptableResource(adaptable, annotatedElement, fieldName);
        }
        return null;
    }

    private LocalDateTime getLocalDateTimeFromAdaptableResource(Object adaptable, AnnotatedElement annotatedElement, String fieldName) {
        try {
            final LocalDateTimeValueMapValue annotation = annotatedElement.getAnnotation(LocalDateTimeValueMapValue.class);
            final Resource resource = (Resource) adaptable;
            return this.getLocalDateTimeValueFromValueMap(annotation, resource, fieldName);
        } catch (final Exception e) {
            log.error("Error during fetching LocalDate from value map", e);
        }
        return null;
    }

    private LocalDateTime getLocalDateTimeFromAdaptableRequest(final Object adaptable, AnnotatedElement annotatedElement, final String fieldName) {
        try {
            final LocalDateTimeValueMapValue annotation = annotatedElement.getAnnotation(LocalDateTimeValueMapValue.class);
            final Resource resource = ((SlingHttpServletRequest) adaptable).getResource();
            return this.getLocalDateTimeValueFromValueMap(annotation, resource, fieldName);
        } catch (final Exception e) {
            log.error("Error during fetching LocalDate from value map", e);
        }
        return null;
    }

    private LocalDateTime getLocalDateTimeValueFromValueMap(final LocalDateTimeValueMapValue annotation, final Resource resource, final String fieldName) {
        if (StringUtils.isNotEmpty(annotation.value()) && resource.hasChildren()) {
            final Resource childResource = resource.getChild(annotation.value());
            if (childResource != null) {
                final Date date = childResource.getValueMap().get(this.getFieldName(annotation, fieldName), Date.class);
                if (date != null) {
                    return this.convertDateToLocalDateTime(date);
                }
            }
        } else {
            final Date date = resource.getValueMap().get(this.getFieldName(annotation, fieldName), Date.class);
            if (date != null) {
                return this.convertDateToLocalDateTime(date);
            }
        }
        return null;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement annotatedElement) {
        LocalDateTimeValueMapValue annotation = annotatedElement.getAnnotation(LocalDateTimeValueMapValue.class);
        if (annotation != null) {
            return new RequestedDateTimeMetadataProviderAnnotationProcessor();
        }
        return null;
    }

    private String getFieldName(final LocalDateTimeValueMapValue annotation, final String fieldName) {
        return StringUtils.isNotEmpty(annotation.name()) ? annotation.name() : fieldName;
    }

    private LocalDateTime convertDateToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}