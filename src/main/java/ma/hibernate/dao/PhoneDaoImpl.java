package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("DAO error when saving a phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneQuery.from(Phone.class);
            Predicate whereFinal = criteriaBuilder.and();
            for (Map.Entry<String, String[]> paramsEntry : params.entrySet()
            ) {
                Path<String> stringPath = phoneRoot.get(paramsEntry.getKey());
                Predicate inPredicate = stringPath.in(Arrays.asList(paramsEntry.getValue()));
                whereFinal = criteriaBuilder.and(whereFinal, inPredicate);
            }
            phoneQuery.where(whereFinal);
            return session.createQuery(phoneQuery).getResultList();
        }
    }
}
