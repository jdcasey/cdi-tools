package org.commonjava.util.cdi;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

final class NamedLiteral
    extends AnnotationLiteral<Named>
    implements Named
{
    private static final long serialVersionUID = 1L;

    private final String name;

    public NamedLiteral( final String name )
    {
        this.name = name;
    }

    @Override
    public String value()
    {
        return name;
    }

}