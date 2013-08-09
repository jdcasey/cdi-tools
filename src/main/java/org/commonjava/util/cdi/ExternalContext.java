package org.commonjava.util.cdi;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;

import org.commonjava.util.logging.Logger;

public class ExternalContext
    implements Context, Iterable<ExternalBean<?>>
{

    private final Logger logger = new Logger( getClass() );

    private final Map<String, ExternalBean<?>> beans = new HashMap<>();

    public ExternalContext with( final Class<?> type, final ExternalBean<?> bean )
    {
        beans.put( key( type, bean.getQualifiers() ), bean );
        return this;
    }

    @Override
    public Class<? extends Annotation> getScope()
    {
        return ExternalScope.class;
    }

    @Override
    public <T> T get( final Contextual<T> contextual, final CreationalContext<T> creationalContext )
    {
        logger.info( "Retrieving instance for: %s", contextual );
        return ( (ExternalBean<T>) contextual ).getInstance();
    }

    @Override
    public <T> T get( final Contextual<T> contextual )
    {
        logger.info( "Retrieving instance for: %s", contextual );
        return ( (ExternalBean<T>) contextual ).getInstance();
    }

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public Iterator<ExternalBean<?>> iterator()
    {
        return beans.values()
                    .iterator();
    }

    @SuppressWarnings( "unchecked" )
    public <T> ExternalBean<T> getBean( final AnnotatedType<T> at )
    {
        return (ExternalBean<T>) beans.get( key( at.getJavaClass(), at.getAnnotations() ) );
    }

    @SuppressWarnings( "unchecked" )
    public <T> ExternalBean<T> getBean( final AnnotatedField<T> af )
    {
        return (ExternalBean<T>) beans.get( key( af.getJavaMember()
                                                   .getType(), af.getAnnotations() ) );
    }

    public boolean contains( final AnnotatedType<?> at )
    {
        return beans.containsKey( key( at.getJavaClass(), at.getAnnotations() ) );
    }

    public boolean contains( final AnnotatedField<?> af )
    {
        return beans.containsKey( key( af.getJavaMember()
                                         .getType(), af.getAnnotations() ) );
    }

    private String key( final Class<?> cls, final Set<Annotation> annos )
    {
        //        final List<String> annoStrs = new ArrayList<String>();
        //        for ( final Annotation annotation : annos )
        //        {
        //            if ( annotation instanceof Named )
        //            {
        //                annoStrs.add( "Named[" + ( (Named) annotation ).value() + "]" );
        //            }
        //            else
        //            {
        //                annoStrs.add( annotation.annotationType()
        //                                        .getSimpleName() );
        //            }
        //        }
        //
        //        Collections.sort( annoStrs );

        return cls.getName(); // + "#" + annoStrs;
    }

}
