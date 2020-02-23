//
//package org.jooq.example.spring.transaction;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.ImportAware;
//import org.springframework.context.annotation.Role;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.AnnotationAttributes;
//import org.springframework.core.type.AnnotationMetadata;
//import org.springframework.lang.Nullable;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.*;
//import org.springframework.transaction.config.TransactionManagementConfigUtils;
//import org.springframework.transaction.event.TransactionalEventListenerFactory;
//import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
//import org.springframework.transaction.interceptor.TransactionAttributeSource;
//import org.springframework.transaction.interceptor.TransactionInterceptor;
//import org.springframework.util.CollectionUtils;
//
//import java.util.Collection;
//
//@Configuration
//public class ProxySuspendTransactionManagementConfiguration implements ImportAware {
//
//	protected PlatformTransactionManager txManager;
//	@Nullable
//	protected AnnotationAttributes enableTx;
//
//	@Bean
//	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//	public BeanFactoryTransactionAttributeSourceAdvisor suspendTransactionAdvisor() {
//		BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
//		advisor.setTransactionAttributeSource(suspendTransactionAttributeSource());
//		advisor.setAdvice(suspendTransactionInterceptor());
//		advisor.setOrder(Ordered.LOWEST_PRECEDENCE);
//		return advisor;
//	}
//	@Bean
//	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//	public TransactionAttributeSource suspendTransactionAttributeSource() {
//		return new AnnotationTransactionAttributeSource(new SuspendTransactionAnnotationParser());
//	}
//
//	@Bean
//	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//	public TransactionalEventListenerFactory transactionalEventListenerFactory() {
//		return new TransactionalEventListenerFactory();
//	}
//	@Override
//	public void setImportMetadata(AnnotationMetadata importMetadata) {
//		this.enableTx = AnnotationAttributes.fromMap(
//				importMetadata.getAnnotationAttributes(EnableTransactionManagement.class.getName(), false));
//		if (this.enableTx == null) {
//			throw new IllegalArgumentException(
//					"@EnableTransactionManagement is not present on importing class " + importMetadata.getClassName());
//		}
//	}
//
//	@Autowired(required = false)
//	void setConfigurers(Collection<TransactionManagementConfigurer> configurers) {
//		if (CollectionUtils.isEmpty(configurers)) {
//			return;
//		}
//		if (configurers.size() > 1) {
//			throw new IllegalStateException("Only one TransactionManagementConfigurer may exist");
//		}
//		TransactionManagementConfigurer configurer = configurers.iterator().next();
//		this.txManager = configurer.annotationDrivenTransactionManager();
//	}
//
//	@Bean
//	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//	public TransactionInterceptor suspendTransactionInterceptor() {
//        TransactionInterceptor interceptor = new SuspendTransactionInterceptor();
//        interceptor.setTransactionAttributeSource(suspendTransactionAttributeSource());
//        if (this.txManager != null) {
//            interceptor.setTransactionManager(this.txManager);
//        }
//        return interceptor;
//	}
//}
