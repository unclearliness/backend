package vn.khanguyen.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.khanguyen.backend.domain.Job;
import vn.khanguyen.backend.domain.Resume;
import vn.khanguyen.backend.domain.User;
import vn.khanguyen.backend.domain.res.resume.ResCreateResumeDTO;
import vn.khanguyen.backend.domain.res.resume.ResResumeDTO;
import vn.khanguyen.backend.domain.res.resume.ResUpdateResumeDTO;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    @Mapping(target = "createBy", source = "createdBy")
    ResCreateResumeDTO toResCreateResumeDTO(Resume resume);

    ResUpdateResumeDTO toResUpdateResumeDTO(Resume resume);

    ResResumeDTO toResResumeDTO(Resume resume);

    ResResumeDTO.UserResume toUserResume(User user);

    ResResumeDTO.JobResume toJobResume(Job job);
}
