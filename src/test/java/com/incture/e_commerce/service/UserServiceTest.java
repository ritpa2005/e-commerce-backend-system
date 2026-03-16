package com.incture.e_commerce.service;

import com.incture.e_commerce.dto.UserDto;
import com.incture.e_commerce.dto.UserRequestDto;
import com.incture.e_commerce.dto.UserResponseDto;
import com.incture.e_commerce.entity.User;
import com.incture.e_commerce.exception.IdNotFoundException;
import com.incture.e_commerce.exception.UserAlreadyPresentException;
import com.incture.e_commerce.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to test methods of UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UserService userService;


    @Test
    void addUser_WhenReceiveNewEmail_ShouldAddUserSuccessfully() {

        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.com");
        User user = new User();
        user.setEmail("test@mail.com");
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(userResponseDto);

        assertNotNull(userService.addUser(userDto));
        verify(userRepository).save(user);
        
    }
    
    @Test
    void addUser_WhenReceiveExistingEmail_ShouldThrowUserAlreadyPresentException() {

        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.com");
        User existingUser = new User();
        existingUser.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(existingUser));

        UserAlreadyPresentException exception = assertThrows(UserAlreadyPresentException.class,
                () -> userService.addUser(userDto));
        verify(userRepository, never()).save(any());
        assertEquals("Email already exists.", exception.getMessage());
        
    }


    @Test
    void getUserById_WhenReceiveValidId_ShouldGetUserSuccessfully() {

        User user = new User();
        user.setId(1L);
        UserRequestDto request = new UserRequestDto();
        request.setUserId(1L);
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(userResponseDto);
        
        assertNotNull(userService.getUserById(request));
        
    }
    
    @Test
    void getUserById_WhenReceiveInvalidId_ShouldThrowIdNotFoundException() {

        UserRequestDto request = new UserRequestDto();
        request.setUserId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        IdNotFoundException exception = assertThrows(IdNotFoundException.class,
                () -> userService.getUserById(request));
        assertEquals("User with id = 2 not found.", exception.getMessage());
        
    }
    
    
    @Test
    void updateUser_WhenReceiveValidIdWithNoEmail_ShouldUpdateUserSuccessfully() {

    	Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setUserId(userId);
        UserDto userDto = new UserDto();
        userDto.setName("New Name");
        User user = new User();
        user.setId(userId);
        user.setName("Old Name");
        
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(modelMapper).map(userDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(responseDto);

        UserResponseDto userResponseDto = userService.updateUser(requestDto, userDto);

        assertNotNull(userResponseDto);
        assertEquals(userId, userResponseDto.getId());
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        
    }
    
    @Test
    void updateUser_WhenReceiveValidIdWithSameEmail_ShouldUpdateUserSuccessfully() {

    	Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setUserId(userId);
        UserDto userDto = new UserDto();
        userDto.setEmail("test@mail.com");
        User user = new User();
        user.setId(userId);
        user.setEmail("test@mail.com");
        
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setEmail("test@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(modelMapper).map(userDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(responseDto);

        UserResponseDto userResponseDto = userService.updateUser(requestDto, userDto);

        assertNotNull(userResponseDto);
        assertEquals(userId, userResponseDto.getId());
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        
    }
    
    @Test
    void updateUser_WhenReceiveValidIdWithNewEmail_ShouldUpdateUserSuccessfully() {

    	Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setUserId(userId);
        UserDto userDto = new UserDto();
        userDto.setEmail("new@mail.com");
        User user = new User();
        user.setId(userId);
        user.setEmail("old@mail.com");
        
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setEmail("new@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(modelMapper).map(userDto, user);
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(responseDto);

        UserResponseDto result = userService.updateUser(requestDto, userDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail("new@mail.com");
        verify(userRepository).save(user);
        
    }
    
    @Test
    void updateUser_WhenReceiveValidIdWithExistingEmail_ShouldThrowUserAlreadyPresentException() {

    	Long userId = 1L;
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setUserId(userId);
        UserDto userDto = new UserDto();
        userDto.setEmail("new@mail.com");
        User user = new User();
        user.setId(userId);
        user.setEmail("old@mail.com");
        User anotherUser = new User();
        anotherUser.setId(userId);
        anotherUser.setEmail("new@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.of(anotherUser));

        UserAlreadyPresentException exception = assertThrows(UserAlreadyPresentException.class,
                () -> userService.updateUser(requestDto, userDto));
        assertEquals("Email already exists.", exception.getMessage());
        verify(userRepository).findByEmail("new@mail.com");
        
    }
    
    @Test
    void updateUser_WhenReceiveInvalidId_ShouldThrowIdNotFoundException() {

        UserRequestDto request = new UserRequestDto();
        request.setUserId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        IdNotFoundException exception = assertThrows(IdNotFoundException.class,
                () -> userService.updateUser(request, any(UserDto.class)));
        assertEquals("User with id = 2 not found.", exception.getMessage());
        verify(userRepository, never()).save(any());
        
    }


    @Test
    void deleteUser_WhenReceiveValidId_ShouldDeleteUserSuccessfully() {

        User user = new User();
        user.setId(1L);
        UserRequestDto request = new UserRequestDto();
        request.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(request);
        
        verify(userRepository).delete(user);
        
    }
    
    @Test
    void deleteUser_WhenReceiveInvalidId_ShouldThrowIdNotFoundException() {

        UserRequestDto request = new UserRequestDto();
        request.setUserId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        IdNotFoundException exception = assertThrows(IdNotFoundException.class,
                () -> userService.deleteUser(request));
        assertEquals("User with id = 2 not found.", exception.getMessage());
        
    }


    @Test
    void loadUserByUsername() {

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        assertNotNull(userService.loadUserByUsername("test@mail.com"));
    
    }
    
    @Test
    void testLoadUserByUsername_NotFound() {

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("test@mail.com"));
    
    }
    
}
