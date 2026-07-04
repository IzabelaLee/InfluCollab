package com.influcollab.repository.spec;

import com.influcollab.entity.CollabOpportunity;
import org.springframework.data.jpa.domain.Specification;

public class CollabOpportunitySpecification {

    public static Specification<CollabOpportunity> hasCity(String city) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<CollabOpportunity> startsOnOrAfter(java.time.LocalDate startDate) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
    }

    public static Specification<CollabOpportunity> endsOnOrBefore(java.time.LocalDate endDate) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("endDate"), endDate);
    }

    public static Specification<CollabOpportunity> hasOwnerId(String ownerId) {
        return (root, query, cb) ->
                cb.equal(root.get("owner").get("id"), ownerId);
    }
}
