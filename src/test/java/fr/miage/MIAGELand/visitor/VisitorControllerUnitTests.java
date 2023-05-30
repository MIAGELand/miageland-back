package fr.miage.MIAGELand.visitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(VisitorController.class)
public class VisitorControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitorRepository visitorRepository;

    @MockBean
    private VisitorService visitorService;

    @WithMockUser(value = "spring")
    @Test
    public void getVisitor() throws Exception {
        given(visitorRepository.findByEmail("test_1"))
                .willReturn(new Visitor("test_1", "test_1", "test_1"));
        String email = "test_1";
        mockMvc.perform(get("/api/visitors/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    private List<Visitor> generateVisitors(int nbVisitors) {
        return Stream.iterate(0, n -> n+1)
                .limit(nbVisitors)
                .map(i -> new Visitor("test_" + i, "test_" + i, "test_" + i))
                .toList();
    }

}
