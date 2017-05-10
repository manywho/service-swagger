package com.manywho.services.swagger.utilities;

import com.manywho.sdk.api.ContentType;
import com.manywho.services.swagger.exception.NotSupportedTypeException;
import io.swagger.models.properties.Property;
import java.util.Objects;

public class TypeConverterUtil {

    public static ContentType convertFromSwaggerToManyWho(String swaggerType, String format) {
        switch (swaggerType) {
            case "integer":
                return ContentType.Number;
            case "boolean":
                return ContentType.Boolean;
            case "string":
                if (Objects.equals(format, "date-time")) {
                    return ContentType.DateTime;
                } else if (Objects.equals(format, null)) {
                    return ContentType.String;
                }
            default:
                throw new NotSupportedTypeException();
        }
    }

    public static Object getPropertyValue(Property swaggerProperty,
                                          com.manywho.sdk.api.run.elements.type.Property manywhoProperty) {
        switch (swaggerProperty.getType()) {
            case "string":
                return manywhoProperty.getContentValue();
            case "integer":
                return Integer.parseInt(manywhoProperty.getContentValue());
        }

        return manywhoProperty.getContentValue();
    }
}
