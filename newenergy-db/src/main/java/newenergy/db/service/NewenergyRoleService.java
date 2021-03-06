package newenergy.db.service;

import newenergy.db.domain.NewenergyRole;
import newenergy.db.repository.NewenergyRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NewenergyRoleService {

    @Autowired
    private NewenergyRoleRepository roleRepository;

    public Set<String> queryByIds(Integer[] roleIds) {
        Set<String> roles = new HashSet<String>();
        if(roleIds.length == 0){
            return roles;
        }
        List<NewenergyRole> roleList = roleRepository.getAllByEnableIsTrueAndDeletedIsFalseAndIdIn(roleIds);
        for(NewenergyRole role : roleList){
            roles.add(role.getName());
        }
        return roles;
    }

    public List<NewenergyRole> querySelective(String name, Integer page, Integer size, String sort){
        //分页
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        Specification specification = getNameSpecification(name);

        return roleRepository.findAll(specification, pageable).getContent();

    }

    public List<NewenergyRole> queryAll(){
        return roleRepository.getAllByEnableIsTrueAndDeletedIsFalse();
    }

    public int countSelective(String name){

        Specification specification = getNameSpecification(name);

        return (int)roleRepository.count(specification);

    }

    private Specification<NewenergyRole> getNameSpecification(String name){
        //动态条件
        Specification<NewenergyRole> specification = new Specification<NewenergyRole>() {
            @Override
            public Predicate toPredicate(Root<NewenergyRole> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(name!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%"+name+"%")));
                }
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("deleted"), false)));
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("enable"), true)));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return specification;
    }
}
