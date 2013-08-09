package org.commonjava.util.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.commonjava.util.logging.Logger;

public class ConfigurationExtension
    implements Extension
{

    private final Logger logger = new Logger( getClass() );

    private final ExternalContext ctx;

    public ConfigurationExtension( final ExternalContext ctx )
    {
        this.ctx = ctx;
    }

    public void afterBeanDiscovery( @Observes final AfterBeanDiscovery event, final BeanManager manager )
    {
        logger.info( "Registering context: %s", ctx );
        event.addContext( ctx );

        for ( final ExternalBean<?> bean : ctx )
        {
            logger.info( "Adding external bean: %s", bean );
            bean.setBeanManager( manager );
            event.addBean( bean );
        }
    }

    <X> void processInjectionTarget( @Observes final ProcessInjectionTarget<X> pit )
    {
        //wrap this to intercept the component lifecycle
        final InjectionTarget<X> it = pit.getInjectionTarget();
        final AnnotatedType<X> at = pit.getAnnotatedType();
        final ExternalBean<X> bean = ctx.getBean( at );

        final InjectionTarget<X> wrapped = new InjectionTarget<X>()
        {

            @Override
            public void inject( final X instance, final CreationalContext<X> ctx )
            {
                it.inject( instance, ctx );
            }

            @Override
            public void postConstruct( final X instance )
            {
                it.postConstruct( instance );
            }

            @Override
            public void preDestroy( final X instance )
            {
                it.preDestroy( instance );
            }

            @Override
            public void dispose( final X instance )
            {
                it.dispose( instance );
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints()
            {
                return it.getInjectionPoints();
            }

            @Override
            public X produce( final CreationalContext<X> ctx )
            {
                logger.info( "Attempting to retrieve external instance for: %s (%s)", at.getJavaClass(), bean );
                return bean == null ? it.produce( ctx ) : bean.getInstance();
            }
        };

        pit.setInjectionTarget( wrapped );

    }

    //    <X> void processAnnotatedType( @Observes final ProcessAnnotatedType<X> pat )
    //    {
    //
    //        //wrap this to override the annotations of the class
    //
    //        final AnnotatedType<X> at = pat.getAnnotatedType();
    //
    //        final AnnotatedType<X> wrapped = new AnnotatedType<X>()
    //        {
    //
    //            @Override
    //            public Set<AnnotatedConstructor<X>> getConstructors()
    //            {
    //                return at.getConstructors();
    //            }
    //
    //            @Override
    //            public Set<AnnotatedField<? super X>> getFields()
    //            {
    //                return at.getFields();
    //            }
    //
    //            @Override
    //            public Class<X> getJavaClass()
    //            {
    //                return at.getJavaClass();
    //            }
    //
    //            @Override
    //            public Set<AnnotatedMethod<? super X>> getMethods()
    //            {
    //                return at.getMethods();
    //            }
    //
    //            @Override
    //            public <T extends Annotation> T getAnnotation( final Class<T> annType )
    //            {
    //                if ( ExternalScope.class.equals( annType ) && ctx.contains( at.getJavaClass() ) )
    //                {
    //                    return annType.cast( new AnnotationLiteral<ExternalScope>()
    //                    {
    //                        private static final long serialVersionUID = 1L;
    //                    } );
    //                }
    //                else
    //                {
    //                    return at.getAnnotation( annType );
    //                }
    //            }
    //
    //            @Override
    //            public Set<Annotation> getAnnotations()
    //            {
    //                return at.getAnnotations();
    //            }
    //
    //            @Override
    //            public Type getBaseType()
    //            {
    //                return at.getBaseType();
    //            }
    //
    //            @Override
    //            public Set<Type> getTypeClosure()
    //            {
    //                return at.getTypeClosure();
    //            }
    //
    //            @Override
    //            public boolean isAnnotationPresent( final Class<? extends Annotation> annType )
    //            {
    //                if ( annType.equals( ExternalContext.class ) )
    //                {
    //                    return ctx.contains( at.getJavaClass() );
    //                }
    //
    //                return at.isAnnotationPresent( annType );
    //            }
    //
    //        };
    //
    //        pat.setAnnotatedType( wrapped );
    //
    //    }

}
