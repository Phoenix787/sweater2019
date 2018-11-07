package com.example.sweater.service;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {


    private final UserRepository repository;
    private final MailSender mailSender;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, MailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;

    }

    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return repository.findByUsername(s);
    }

    public boolean addUser(User user) {
        User userFromDb = repository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return false; //будет означать что пользователь не добавлен
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);

        sendMessage(user);

        return true;

    }

    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {

            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater. Please visit next link: http://localhost:8090/activate/%s",
                    user.getUsername(), user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Activate", message);
        }
    }

    public boolean activateUser(String code) {
        User user = repository.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null); // это означает что пользователь подтвердил активацию
        repository.save(user);
        return true;

    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values()).
                map(Role::name).collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    public void updateProfile(User user, String password, String email) {
        String currentEmail = user.getEmail();

        boolean isEmailChanged = (email != null & !email.equals(currentEmail))
                || (currentEmail != null && !currentEmail.equals(email));

        if (isEmailChanged) {
            user.setEmail(email);

            //отправляем новый активационный код
            if (!StringUtils.isEmpty(email)){
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        repository.save(user);

        if (isEmailChanged) {
            sendMessage(user);
        }
    }

}
