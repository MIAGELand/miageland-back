package fr.miage.MIAGELand.visitor;

import fr.miage.MIAGELand.ticket.Ticket;
import fr.miage.MIAGELand.ticket.TicketRepository;
import fr.miage.MIAGELand.ticket.TicketState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
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
                .willReturn(generateVisitors(1).get(0));
        String email = "test_1";
        mockMvc.perform(get("/api/visitors/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    private List<Visitor> generateVisitors(int nbVisitors) {
        return Stream.iterate(1, n -> n+1)
                .limit(nbVisitors)
                .map(i -> new Visitor("test_" + i, "test_" + i, "test_" + i))
                .toList();
    }

    private List<Ticket> generateTickets(int nbTickets, Visitor visitor) {
        return Stream.iterate(1, n -> n+1)
                .limit(nbTickets)
                .map(i -> new Ticket(visitor, LocalDate.now(), 10.0f, TicketState.RESERVED))
                .toList();
    }

}
