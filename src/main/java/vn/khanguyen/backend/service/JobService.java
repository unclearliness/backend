package vn.khanguyen.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.khanguyen.backend.domain.Company;
import vn.khanguyen.backend.domain.Job;
import vn.khanguyen.backend.domain.Skill;
import vn.khanguyen.backend.domain.dto.Meta;
import vn.khanguyen.backend.domain.dto.ResultPaginationDTO;
import vn.khanguyen.backend.domain.res.job.ResJobDTO;
import vn.khanguyen.backend.repository.JobRepository;
import vn.khanguyen.backend.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final CompanyService companyService;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, CompanyService companyService, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
        this.skillRepository = skillRepository;
    }

    public Job createJob(Job job) {
        // check company
        if (job.getCompany() != null) {
            Company companyOpt = this.companyService.findById(job.getCompany().getId());
            if (companyOpt != null) {
                job.setCompany(companyOpt);
            }
        }

        // check skills (many-to-many, no cascade)
        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            List<Long> skillIds = job.getSkills().stream().map(Skill::getId).toList();
            job.setSkills(this.skillRepository.findAllById(skillIds));
        }

        return this.jobRepository.save(job);
    }

    public Job updateJob(Job jobCur) {
        Job job = this.jobRepository.findById(jobCur.getId()).orElse(null);
        if (job == null) {
            return null;
        }

        job.setName(jobCur.getName());
        job.setLocation(jobCur.getLocation());
        job.setSalary(jobCur.getSalary());
        job.setQuantity(jobCur.getQuantity());
        job.setLevel(jobCur.getLevel());
        job.setDescription(jobCur.getDescription());
        job.setStartDate(jobCur.getStartDate());
        job.setEndDate(jobCur.getEndDate());
        job.setActive(jobCur.isActive());

        // check company
        if (jobCur.getCompany() != null) {
            Company companyOpt = this.companyService.findById(jobCur.getCompany().getId());
            if (companyOpt != null) {
                job.setCompany(companyOpt);
            }
        }

        // check skills
        if (jobCur.getSkills() != null) {
            List<Long> skillIds = jobCur.getSkills().stream().map(Skill::getId).toList();
            job.setSkills(this.skillRepository.findAllById(skillIds));
        }

        return this.jobRepository.save(job);
    }

    public Job deleteJob(Long id) {
        Job job = this.jobRepository.findById(id).orElse(null);
        if (job == null) {
            return null;
        }

        this.jobRepository.delete(job);
        return job;
    }

    public Job getJobById(long id) {
        return this.jobRepository.findById(id).orElse(null);
    }

    public boolean isJobExist(long id) {
        return this.jobRepository.existsById(id);
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageJob.getNumber() + 1);
        mt.setPageSize(pageJob.getSize());
        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);
        List<ResJobDTO> listJobs = pageJob.getContent().stream().map(this::convertToJobDTO).toList();
        rs.setResult(listJobs);

        return rs;
    }

    public ResJobDTO convertToJobDTO(Job job) {
        ResJobDTO res = new ResJobDTO();
        res.setId(job.getId());
        res.setName(job.getName());
        res.setLocation(job.getLocation());
        res.setSalary(job.getSalary());
        res.setQuantity(job.getQuantity());
        res.setLevel(job.getLevel());
        res.setDescription(job.getDescription());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setActive(job.isActive());
        res.setCreatedAt(job.getCreatedAt());
        res.setUpdatedAt(job.getUpdatedAt());
        res.setCreatedBy(job.getCreatedBy());
        res.setUpdatedBy(job.getUpdatedBy());

        if (job.getSkills() != null) {
            res.setSkill(job.getSkills().stream().map(Skill::getName).toList());
        }

        if (job.getCompany() != null) {
            res.setCompany(job.getCompany());
        }

        return res;
    }
}
