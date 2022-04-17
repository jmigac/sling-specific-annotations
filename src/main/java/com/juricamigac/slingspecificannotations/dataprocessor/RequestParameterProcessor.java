package com.juricamigac.slingspecificannotations.dataprocessor;

import com.juricamigac.slingspecificannotations.annotations.RequestParameter;
import com.juricamigac.slingspecificannotations.constants.ServiceRankingConstants;
import com.juricamigac.slingspecificannotations.dataprocessor.annotationsprocessor.RequestParameterMetadataProviderAnnotationProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import static org.osgi.framework.Constants.SERVICE_RANKING;

@Component(
        immediate = true,
        service = {Injector.class, StaticInjectAnnotationProcessorFactory.class},
        property = {SERVICE_RANKING + ":Integer=" + ServiceRankingConstants.SLING_ANNOTATIONS_SERVICE_RANKING})
public class RequestParameterProcessor implements Injector, StaticInjectAnnotationProcessorFactory {

    private static final String REQUEST_PARAMETER_SOURCE = "request-parameter-sling-specific-injection";

    @Override
    public String getName() {
        return REQUEST_PARAMETER_SOURCE;
    }

    @Override
    public Object getValue(Object adaptable, String fieldName, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
        if (adaptable instanceof SlingHttpServletRequest && this.isRequestParameterAndFieldIsString(type, annotatedElement)) {
            final RequestParameter annotation = annotatedElement.getAnnotation(RequestParameter.class);
            final String parameterName = this.getParameterName(fieldName, annotation);
            final SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
            return request.getParameter(parameterName);
        }
        return null;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement annotatedElement) {
        final RequestParameter annotation = annotatedElement.getAnnotation(RequestParameter.class);
        if (annotation != null) {
            return new RequestParameterMetadataProviderAnnotationProcessor();
        }
        return null;
    }

    private boolean isRequestParameterAndFieldIsString(final Type type, final AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(RequestParameter.class) && type.equals(String.class);
    }

    private String getParameterName(final String fieldName, final RequestParameter annotation) {
        return StringUtils.isNotEmpty(annotation.name()) ? annotation.name() : fieldName;
    }

}
