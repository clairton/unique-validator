package br.eti.clairton.uniquevalidator;

import static javax.enterprise.inject.spi.CDI.current;

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
	private String path;
	private Hint[] hints;
	

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
		path = annotation.path();
		hints = annotation.hints();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public boolean isValid(final Object record, final ConstraintValidatorContext context) {
		final Class<EntityManager> t = EntityManager.class;
		final Instance<EntityManager> select = current().select(t, qualifier);
		final EntityManager em = select.get();
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<?> root = cq.from(type);
		final Path<?> field = root.get(path);
		final Object value = getValue(record, record.getClass(), path);
		
		cq.select(cb.count(root));
		final Predicate equal = cb.equal(field, value);

    	final String name = getIdField(em, record.getClass());
    	final Object id = getValue(record, record.getClass(), name);
        if(id != null){
        	final Path<?> fieldId = root.get(name);
        	final Predicate notEqual = cb.notEqual(fieldId, id);	        	
        	cq.where(cb.and(equal, notEqual));
        }else{
        	cq.where(equal);
        }	    
		
		final TypedQuery<Long> query = em.createQuery(cq);
		final FlushModeType flushMode = query.getFlushMode();
		query.setFlushMode(FlushModeType.COMMIT);
		for (final Hint hint : hints) {
			query.setHint(hint.key(), hint.value());			
		}
		
		final long count = query.getSingleResult();
		query.setFlushMode(flushMode);
		final Boolean isValid = count == 0l;

		if(!isValid){
            context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{br.eti.clairton.uniquevalidator.Unique.message}")
				.addPropertyNode(path)
				.addConstraintViolation();				
		}
		return isValid;
	}

	protected String getIdField(final EntityManager manager, final Class<?> type){
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
	

	protected Object getValue(final Object object, final Class<?> type, final String attribute) {
        try{	
        	return getField(type, attribute).get(object);
		}catch(final Exception e){
			throw new RuntimeException(e);
		}
	}
	
	protected Field getField(final Class<?> type, final String attribute) throws NoSuchFieldException {
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
