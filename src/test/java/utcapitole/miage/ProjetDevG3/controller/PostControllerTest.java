package utcapitole.miage.projetdevg3.controller;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "test@example.com")  // même nom que dans les mocks
class PostControllerTest {
    

}
