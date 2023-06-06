package com.surajprojects.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.surajprojects.ecommerce.entity.Country;
import com.surajprojects.ecommerce.entity.Order;
import com.surajprojects.ecommerce.entity.Product;
import com.surajprojects.ecommerce.entity.ProductCategory;
import com.surajprojects.ecommerce.entity.State;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;

@Configuration
public class DataRestConfig implements RepositoryRestConfigurer {

	@Value("${allowed.origins}")
	private String[] theAllowedOrigins;
	
	private EntityManager entityManager;

	public DataRestConfig(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

		HttpMethod[] unsupportedActions = { HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH };

		// disable HTTP methods for All APIs: PUT, POST and DELETE
		disableHttpMethods(ProductCategory.class,config, unsupportedActions);
		disableHttpMethods(Product.class,config, unsupportedActions);
		disableHttpMethods(Country.class,config, unsupportedActions);
		disableHttpMethods(State.class,config, unsupportedActions);
		disableHttpMethods(Order.class,config, unsupportedActions);

		// call an internal helper method
		exposeIds(config);
		
		//configure cors mapping
		cors.addMapping(config.getBasePath()+"/**").allowedOrigins(theAllowedOrigins);
	}

	private ExposureConfigurer disableHttpMethods(Class theClass, RepositoryRestConfiguration config, HttpMethod[] unsupportedActions) {
		return config.getExposureConfiguration().forDomainType(theClass)
				.withItemExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedActions))
				.withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(unsupportedActions));
	}

	private void exposeIds(RepositoryRestConfiguration config) {

		Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

		List<Class> entityClasses = new ArrayList<>();

		for (EntityType tempEntityType : entities) {
			entityClasses.add(tempEntityType.getJavaType());
		}

		Class[] domainTypes = entityClasses.toArray(new Class[0]);
		config.exposeIdsFor(domainTypes);
	}
}
