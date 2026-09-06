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
import ng.teamTaskBoard.exceptions.InvalidCredentialsException;
import ng.teamTaskBoard.exceptions.MemberNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private AuthService authService;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(memberRepository);
    }

    @Test
    public void testThatMemberCanRegisterSuccessfully() {
        RegisterMemberRequest request = new RegisterMemberRequest();
        request.setName("Yomi Adisa");
        request.setEmail("yomi@semicolon.com");
        request.setPassword("Password123");
        request.setRole(Role.MEMBER);

        when(memberRepository.findByEmail("yomi@semicolon.com")).thenReturn(Optional.empty());

        Member savedMember = new Member();
        savedMember.setId(1);
        savedMember.setName("Yomi Adisa");
        savedMember.setEmail("yomi@semicolon.com");
        savedMember.setPassword("Password123");
        savedMember.setRole(Role.MEMBER);
        savedMember.setActive(true);

        when(memberRepository.save(org.mockito.ArgumentMatchers.any(Member.class))).thenReturn(savedMember);

        RegisterMemberResponse response = authService.register(request);

        assertEquals(1, response.getId());
        assertEquals("Yomi Adisa", response.getName());
        assertEquals("yomi@semicolon.com", response.getEmail());
        assertEquals(Role.MEMBER, response.getRole());

        verify(memberRepository).save(org.mockito.ArgumentMatchers.any(Member.class));
    }

    @Test
    public void testThatMemberCanLoginSuccessfully() {
        LoginMemberRequest request = new LoginMemberRequest();
        request.setEmail("yomi@semicolon.com");
        request.setPassword("Password123");

        Member existingMember = new Member();
        existingMember.setId(1);
        existingMember.setName("Yomi Adisa");
        existingMember.setEmail("yomi@semicolon.com");
        existingMember.setPassword("Password123");
        existingMember.setRole(Role.MEMBER);
        existingMember.setActive(false);

        when(memberRepository.findByEmail("yomi@semicolon.com")).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(org.mockito.ArgumentMatchers.any(Member.class))).thenReturn(existingMember);

        LoginMemberResponse response = authService.login(request);

        assertEquals(1, response.getId());
        assertEquals("Yomi Adisa", response.getName());
        assertEquals("yomi@semicolon.com", response.getEmail());
        assertEquals(Role.MEMBER, response.getRole());
        assertTrue(existingMember.isActive());
    }

