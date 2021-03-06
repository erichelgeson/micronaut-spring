package io.micronaut.spring.boot.annotation;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.inject.visitor.VisitorContext;
import io.micronaut.spring.annotation.AbstractSpringAnnotationMapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConditionalOnPropertyAnnotationMapper extends AbstractSpringAnnotationMapper {
    @Override
    public String getName() {
        return "org.springframework.boot.autoconfigure.condition.ConditionalOnProperty";
    }

    @Override
    protected List<AnnotationValue<?>> mapInternal(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final String[] propertyNames = annotation.getValue(String[].class).orElseGet(() -> annotation.get("name", String[].class).orElse(null));
        if (propertyNames != null) {
            final String prefix = annotation.get("prefix", String.class).orElse(null);
            final boolean matchIfMissing = annotation.get("matchIfMissing", boolean.class).orElse(false);
            final String havingValue = annotation.get("havingValue", String.class).orElse(null);
            List<AnnotationValue<?>> annotationValues = new ArrayList<>(propertyNames.length);
            for (String propertyName : propertyNames) {
                if (prefix != null) {
                    propertyName = prefix + "." + propertyName;
                }

                final AnnotationValueBuilder<Requires> builder = AnnotationValue.builder(Requires.class);
                builder.member(matchIfMissing ? "missingProperty" : "property", propertyName);
                if (havingValue != null) {
                    builder.value(havingValue);
                }
                annotationValues.add(builder.build());

            }

            return annotationValues;
        }
        return Collections.emptyList();
    }
}
