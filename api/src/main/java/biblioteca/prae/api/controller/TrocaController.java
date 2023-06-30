package biblioteca.prae.api.controller;

import biblioteca.prae.api.domain.historico.*;
import biblioteca.prae.api.domain.livro.*;
import biblioteca.prae.api.domain.usuario.Usuario;
import biblioteca.prae.api.domain.usuario.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

//-Devo receber o e-mail do usuário e o id dos livros na troca
//-Quando receber tem que pegar os dados desse usuário e os dados dos livros
//-Retornar uma lista de objetos que cada objeto tem id, dados do usuário, e cada livro;


@RestController
@RequestMapping("/trocas")
@SecurityRequirement(name = "bearer-key")
@PreAuthorize("hasRole('ROLE_ADMIN')")

public class TrocaController {
    @Autowired
    private TrocaRepository trocaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping
    public ResponseEntity<Page<DadosListagemTroca>> mostrar(Pageable paginacao) {
        var page = trocaRepository.findAll(paginacao).map(DadosListagemTroca::new);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    @Transactional
    public ResponseEntity adicionar(@RequestBody DadosTroca dadosTroca, UriComponentsBuilder uriBuilder) {
        Usuario usuario = (Usuario) usuarioRepository.findByEmail(dadosTroca.emailUsuario());
        Livro livroEntrada = livroRepository.findById(dadosTroca.livroEntradaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro de entrada não encontrado"));
        Livro livroSaida = livroRepository.findById(dadosTroca.livroSaidaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro de saída não encontrado"));
        Troca troca = new Troca(usuario, livroEntrada, livroSaida, dadosTroca.data());
        trocaRepository.save(troca);
        return ResponseEntity.ok(new DadosDetalhamentoTroca(troca));
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        trocaRepository.deleteById(id);
    }
}
