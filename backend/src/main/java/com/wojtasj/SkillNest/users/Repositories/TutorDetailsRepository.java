package com.wojtasj.SkillNest.users.Repositories;

import com.wojtasj.SkillNest.users.Entities.Role;
import com.wojtasj.SkillNest.users.Entities.TutorDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorDetailsRepository extends JpaRepository<TutorDetails, Long> {

}
