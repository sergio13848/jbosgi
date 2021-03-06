/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.osgi.example.jmx.bundle;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A Service Activator
 * 
 * @author thomas.diesler@jboss.com
 * @since 24-Apr-2009
 */
public class MBeanActivator implements BundleActivator {
    
    @SuppressWarnings({ "rawtypes" })
    public void start(BundleContext context) {
        ServiceTracker tracker = new ServiceTracker<MBeanServer, MBeanServer>(context, MBeanServer.class.getName(), null) {
            public MBeanServer addingService(ServiceReference<MBeanServer> reference) {
                MBeanServer mbeanServer = super.addingService(reference);
                registerMBean(mbeanServer);
                return mbeanServer;
            }

            @Override
            public void removedService(ServiceReference<MBeanServer> reference, MBeanServer service) {
                unregisterMBean(service);
                super.removedService(reference, service);
            }
        };
        tracker.open();
    }

    public void stop(BundleContext context) {
        ServiceReference<MBeanServer> sref = context.getServiceReference(MBeanServer.class);
        if (sref != null) {
            MBeanServer mbeanServer = context.getService(sref);
            unregisterMBean(mbeanServer);
        }
    }

    private void registerMBean(MBeanServer mbeanServer) {
        try {
            ObjectName oname = ObjectName.getInstance(FooMBean.MBEAN_NAME);
            mbeanServer.registerMBean(new Foo(), oname);
        } catch (JMException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void unregisterMBean(MBeanServer mbeanServer) {
        try {
            ObjectName oname = ObjectName.getInstance(FooMBean.MBEAN_NAME);
            if (mbeanServer.isRegistered(oname))
                mbeanServer.unregisterMBean(oname);
        } catch (JMException ex) {
            throw new IllegalStateException(ex);
        }
    }
}