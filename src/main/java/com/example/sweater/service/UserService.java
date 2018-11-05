package com.example.sweater.service;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {



    private final UserRepository repository;
    private  final MailSender mailSender;

    @Autowired
    public UserService(UserRepository repository, MailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return repository.findByUsername(s);
    }

    public boolean addUser(User user){
        User userFromDb = repository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return false; //будет означать что пользователь не добавлен
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        repository.save(user);

        if (!StringUtils.isEmpty(user.getEmail())) {

            String message = String.format(
                    "Hello, %s! \n"+
                            "Welcome to Sweater. Please visit next link: http://localhost:8090/activate/%s",
                    user.getUsername(), user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Activate", message);
        }

        return true;

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
}
