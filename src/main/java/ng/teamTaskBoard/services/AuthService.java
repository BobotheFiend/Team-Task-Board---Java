package ng.teamTaskBoard.services;

import ng.teamTaskBoard.data.models.Member;
import ng.teamTaskBoard.data.models.enums.Role;
import ng.teamTaskBoard.data.repositories.MemberRepository;
import ng.teamTaskBoard.dtos.requests.RegisterMemberRequest;
import ng.teamTaskBoard.dtos.requests.LoginMemberRequest;
import ng.teamTaskBoard.dtos.requests.LogoutMemberRequest;
import ng.teamTaskBoard.dtos.responses.RegisterMemberResponse;
import ng.teamTaskBoard.dtos.responses.LoginMemberResponse;
import ng.teamTaskBoard.dtos.responses.LogoutMemberResponse;
import ng.teamTaskBoard.exceptions.MemberAlreadyExistsException;
import ng.teamTaskBoard.exceptions.InvalidCredentialsException;
import ng.teamTaskBoard.exceptions.MemberNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final MemberRepository memberRepository;

    public AuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public RegisterMemberResponse register(RegisterMemberRequest request) {
        Optional<Member> existingMember = memberRepository.findByEmail(request.getEmail());

        if (existingMember.isPresent()) {
            throw new MemberAlreadyExistsException("Member with email " + request.getEmail() + " already exists");
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPassword(request.getPassword());

        if (request.getRole() == null) {
            member.setRole(Role.MEMBER);
        } else {
            member.setRole(request.getRole());
        }

        member.setActive(true);

        Member savedMember = memberRepository.save(member);

        RegisterMemberResponse response = new RegisterMemberResponse();
        response.setId(savedMember.getId());
        response.setName(savedMember.getName());
        response.setEmail(savedMember.getEmail());
        response.setRole(savedMember.getRole());

        return response;
    }

    public LoginMemberResponse login(LoginMemberRequest request) {
        Optional<Member> existingMember = memberRepository.findByEmail(request.getEmail());

        if (existingMember.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Member member = existingMember.get();

        if (member.getPassword().equals(request.getPassword()) == false) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        member.setActive(true);
        Member savedMember = memberRepository.save(member);

        LoginMemberResponse response = new LoginMemberResponse();
        response.setId(savedMember.getId());
        response.setName(savedMember.getName());
        response.setEmail(savedMember.getEmail());
        response.setRole(savedMember.getRole());

        return response;
    }

    public LogoutMemberResponse logout(LogoutMemberRequest request) {
        Optional<Member> existingMember = memberRepository.findByEmail(request.getEmail());

        if (existingMember.isEmpty()) {
            throw new MemberNotFoundException("Member with email " + request.getEmail() + " not found");
        }

        Member member = existingMember.get();
        member.setActive(false);
        memberRepository.save(member);

        LogoutMemberResponse response = new LogoutMemberResponse();
        response.setEmail(member.getEmail());
        response.setMessage("Logged out successfully");

        return response;
    }
}