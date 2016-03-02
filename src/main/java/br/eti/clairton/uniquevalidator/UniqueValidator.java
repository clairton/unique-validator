package br.eti.clairton.uniquevalidator;

import static javax.enterprise.inject.spi.CDI.current;
import static javax.persistence.FlushModeType.COMMIT;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {
	private Annotation qualifier;
	private Class<?> type;
	private String[] paths;
	private Hint[] hints;
	private static Reader reader = new Reader();
	

	/**
	 * {@inheritDoc}.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void initialize(final Unique annotation) {
		qualifier = new AnnotationLiteral() {
			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("unchecked")
			public Class annotationType() {
				return annotation.qualifier();
			}
		};
		type = annotation.type();
		paths = annotation.path();
		hints = annotation.hints();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public boolean isValid(final Object record, final ConstraintValidatorContext context) {
		final Boolean isValid = isValid(record);
		if(!isValid){
            context.disableDefaultConstraintViolation();
            final String key = "{br.eti.clairton.uniquevalidator.Unique.message}";
			for (final String path : paths) {				
				context.buildConstraintViolationWithTemplate(key)
					.addPropertyNode(path)
					.addConstraintViolation();				
			}
		}
		return isValid;
	}
	
	protected boolean isValid(final Object record) {
		final Long count = reader.count(record, type, paths, hints, qualifier);
		final Boolean isValid = count == 0l;
		return isValid;
	}
	
	public static void mockCountToReturn(final Long count){
		reader = new Reader(){
			@Override
			protected Long count(final Object record, final Class<?> type, final String[] paths, final Hint[] hints, final Annotation qualifier) {
				return count;
			}
		};
	}
	
	public static void mockRollback(){
		reader = new Reader();
	}
	
	private static class Reader{

		protected Long count(final Object record, final Class<?> type, final String[] paths, final Hint[] hints, final Annotation qualifier) {
			final Class<EntityManager> t = EntityManager.class;
			final Instance<EntityManager> select = current().select(t, qualifier);
			final EntityManager em = select.get();
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			final Root<?> root = cq.from(type);
			
			Predicate predicate;
			
			final String name = getIdField(em, record.getClass());
	    	final Object id = getValue(record, record.getClass(), name);
	        if(id != null){
	        	final Path<?> fieldId = root.get(name);
	        	predicate = cb.notEqual(fieldId, id);	        	
	        } else {
	        	predicate = cb.equal(cb.literal(1), cb.literal(1));
	        }
			
			for (final String path : paths) {
				final Path<?> field = root.get(path);
				final Object value = getValue(record, record.getClass(), path);
				final Predicate equal = cb.equal(field, value);
				predicate = cb.and(predicate, equal);
			}		
			
			cq.select(cb.count(root)).where(predicate); 
			
			final TypedQuery<Long> query = em.createQuery(cq);
			final FlushModeType flushMode = query.getFlushMode();
			query.setFlushMode(COMMIT);
			for (final Hint hint : hints) {
				query.setHint(hint.key(), hint.value());			
			}
			
			final Long count = query.getSingleResult();
			query.setFlushMode(flushMode);
			return count;
		}	

		private String getIdField(final EntityManager manager, final Class<?> type){
			final EntityType<?> entity = manager.getMetamodel().entity(type);
			SingularAttribute<?, ?> attribute = null;
			final Set<?> attributes = entity.getSingularAttributes();
	        for (final Object object : attributes) {
	        	attribute = (SingularAttribute<?, ?>) object;
	            if (attribute.isId()){    
	                break;
	            }
	        }

	        if(attribute != null){
	        	return attribute.getName();
	        }else{
	        	throw new RuntimeException("Is not possible find id attribute in " + type);
	        }
		}
		

		private Object getValue(final Object object, final Class<?> type, final String attribute) {
	        try{	
	        	return getField(type, attribute).get(object);
			}catch(final Exception e){
				throw new RuntimeException(e);
			}
		}
		
		private Field getField(final Class<?> type, final String attribute) throws NoSuchFieldException {
		    while (type != null && type != Object.class) {
		    	try{
		        	final Field field = type.getDeclaredField(attribute);
		        	field.setAccessible(true);
		        	return field;
		    	}catch(final NoSuchFieldException e){
		    		return getField(type.getSuperclass(), attribute);
		    	}
		    }
		    throw new NoSuchFieldException();
		}	
	}
}
