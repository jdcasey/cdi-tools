package org.commonjava.util.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;

public class DefaultInstanceBean<T>
    implements ExternalBean<T>
{
    //    private final Logger logger = new Logger( getClass() );

    private final T instance;

    private AnnotatedType<? extends T> at;

    private InjectionTarget<? extends T> it;

    private final Class<T> type;

    public DefaultInstanceBean( final T instance, final Class<T> type )
    {
        this.instance = instance;
        this.type = type;
    }

    @Override
    public T getInstance()
    {
        return instance;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void setBeanManager( final BeanManager bm )
    {
        at = (AnnotatedType<? extends T>) bm.createAnnotatedType( instance.getClass() );
        it = bm.createInjectionTarget( at );
    }

    @Override
    public T create( final CreationalContext<T> creationalContext )
    {
        return instance;
    }

    @Override
    public void destroy( final T instance, final CreationalContext<T> creationalContext )
    {
    }

    @Override
    public Set<Type> getTypes()
    {
        final Set<Type> types = new HashSet<>();
        types.add( instance.getClass() );
        types.add( type );
        types.add( Object.class );

        //        logger.info( "\n\n\nReturning getTypes() == %s\n\n\n", types );
        return types;
    }

    @SuppressWarnings( "serial" )
    @Override
    public Set<Annotation> getQualifiers()
    {
        final Set<Annotation> qualifiers = new HashSet<>();
        qualifiers.add( new AnnotationLiteral<Default>()
        {
        } );

        qualifiers.add( new AnnotationLiteral<Any>()
        {
        } );

        //        qualifiers.add( new AnnotationLiteral<TestData>()
        //        {
        //        } );

        //        qualifiers.add( new AnnotationLiteral<ExternalScope>()
        //        {
        //        } );

        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope()
    {
        return ExternalScope.class;
    }

    @Override
    public String getName()
    {
        return instance.toString();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes()
    {
        return Collections.emptySet();
    }

    @Override
    public Class<?> getBeanClass()
    {
        //        logger.info( "\n\n\nReturning getBeanClass() == %s\n\n\n", instance.getClass()
        //                                                                           .getName() );
        return instance.getClass();
    }

    @Override
    public boolean isAlternative()
    {
        return false;
    }

    @Override
    public boolean isNullable()
    {
        return false;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints()
    {
        return it.getInjectionPoints();
    }

}
