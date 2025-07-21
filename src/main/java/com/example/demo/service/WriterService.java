package com.example.demo.service;

import com.example.demo.model.Writer;
import com.example.demo.repository.WriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WriterService implements UserDetailsService {
    @Autowired
    private WriterRepository writerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Writer> writer = writerRepository.findByUsername(username);
        if(writer.isPresent()){
            var obj = writer.get();
            return User.builder()
                    .username(obj.getUsername())
                    .password(obj.getPassword())
                    .roles(getRoles(obj))
                    .build();
        } else{
            throw new UsernameNotFoundException(username);
        }
    }

    public String[] getRoles(Writer writer){
        if(writer.getRole()==null){
            System.out.println("Always this mf");
            return new String[]{"USER"};
        }
        return writer.getRole().split(",");
    }
}
