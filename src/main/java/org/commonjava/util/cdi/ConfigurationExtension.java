package org.commonjava.util.cdi;

import java.lang.reflect.Field;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

public class ConfigurationExtension
    implements Extension
{

    private final ExternalContext ctx;

    public ConfigurationExtension( final ExternalContext ctx )
    {
        this.ctx = ctx;
    }

    public ExternalContext getContext()
    {
        return ctx;
    }

    public void afterBeanDiscovery( @Observes final AfterBeanDiscovery event, final BeanManager manager )
    {
        event.addContext( ctx );

        for ( final ExternalBean<?> bean : ctx )
        {
            bean.setBeanManager( manager );
            event.addBean( bean );
        }
    }

    <X> void processInjectionTarget( @Observes final ProcessInjectionTarget<X> pit, final BeanManager bm )
    {
        //wrap this to intercept the component lifecycle
        final InjectionTarget<X> it = pit.getInjectionTarget();
        final AnnotatedType<X> at = pit.getAnnotatedType();

        final InjectionTarget<X> wrapped = new InjectionTarget<X>()
        {

            @Override
            public void inject( final X instance, final CreationalContext<X> cc )
            {
                it.inject( instance, cc );

                for ( final AnnotatedField<? super X> field : at.getFields() )
                {
                    final ExternalBean<? super X> bean = ctx.getBean( field );
                    if ( bean != null )
                    {
                        final Field f = field.getJavaMember();
                        f.setAccessible( true );
                        try
                        {
                            f.set( instance, bean.getInstance() );
                        }
                        catch ( final IllegalAccessException e )
                        {
                            throw new RuntimeException( "Failed to inject external bean: " + bean + " into: " + f, e );
                        }
                    }
                }
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
                return it.produce( ctx );
            }
        };

        pit.setInjectionTarget( wrapped );
    }

}
