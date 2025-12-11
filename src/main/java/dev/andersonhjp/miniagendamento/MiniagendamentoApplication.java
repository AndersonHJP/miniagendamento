package dev.andersonhjp.miniagendamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class MiniagendamentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniagendamentoApplication.class, args);
	}

}
