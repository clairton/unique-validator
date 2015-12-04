package br.eti.clairton.uniquevalidator;

import static javax.enterprise.inject.spi.CDI.current;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {
	private Annotation qualifier;
	private Class<?> type;
	private String path;
	

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
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		final Class<EntityManager> t = EntityManager.class;
		final Instance<EntityManager> select = current().select(t, qualifier);
		final EntityManager em = select.get();
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<?> root = cq.from(type);
		final Path<?> field = root.get(path);
		final Predicate equal = cb.equal(field, value);
		cq.select(cb.count(root));
		cq.where(equal);
		final TypedQuery<Long> query = em.createQuery(cq);
		query.setHint("eclipselink.read-only", true);
		query.setHint("org.hibernate.readOnly", true);
		final long count = query.getSingleResult();
		return count == 0l;
	}
}
