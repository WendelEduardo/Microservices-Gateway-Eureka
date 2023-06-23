package br.com.alurafood.pagamentos.controller;

import br.com.alurafood.pagamentos.dto.PagamentoDTO;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<Page<PagamentoDTO>> listar(@PageableDefault(size = 10) Pageable paginacao){
        return ResponseEntity.ok(pagamentoService.listar(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> detalhar(@PathVariable("id") @NotNull Long id){
        return ResponseEntity.ok(pagamentoService.obterPorId(id));
    }

    @PostMapping
    public ResponseEntity<PagamentoDTO> salvar(@RequestBody PagamentoDTO pagamentoDTO, UriComponentsBuilder uriBuilder){
        PagamentoDTO pagamento = pagamentoService.criarPagamento(pagamentoDTO);
        URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        return ResponseEntity.created(endereco).body(pagamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> atualizar(@PathVariable("id") Long id, @RequestBody PagamentoDTO pagamentoDTO){
        return ResponseEntity.ok(pagamentoService.atualizarPagamento(id, pagamentoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id){
        pagamentoService.deletarPagamento(id);
        return ResponseEntity.noContent().build();
    }

    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    @PatchMapping("/{id}/confirmar")
    public void confirmarPagamento(@PathVariable("id") Long id){
        pagamentoService.confirmarPagamento(id);
    }


    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        pagamentoService.alterarStatus(id);
    }
}
