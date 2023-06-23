package br.com.alurafood.pagamentos.service;

import br.com.alurafood.pagamentos.dto.PagamentoDTO;
import br.com.alurafood.pagamentos.dto.PedidoDTO;
import br.com.alurafood.pagamentos.http.PedidoClient;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PedidoClient pedidoClient;

    public Page<PagamentoDTO> listar(Pageable paginacao){
        return pagamentoRepository.findAll(paginacao).map(e -> mapper.map(e, PagamentoDTO.class));
    }

    public PagamentoDTO obterPorId(Long id){
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        PagamentoDTO pagamentoDTO = mapper.map(pagamento, PagamentoDTO.class);

        pagamentoDTO.setItens(pedidoClient.listarPorId(id).getBody().getItens());

        return pagamentoDTO;
    }

    public PagamentoDTO criarPagamento(PagamentoDTO pagamentoDTO){
        Pagamento pagamento = mapper.map(pagamentoDTO, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);

        return mapper.map(pagamento, PagamentoDTO.class);
    }

    public PagamentoDTO atualizarPagamento(Long id, PagamentoDTO pagamentoDTO){
        this.obterPorId(id);

        Pagamento pagamento = mapper.map(pagamentoDTO, Pagamento.class);
        pagamento.setId(id);
        pagamento = pagamentoRepository.save(pagamento);

        return mapper.map(pagamento, PagamentoDTO.class);
    }

    public void deletarPagamento(Long id){
        pagamentoRepository.deleteById(id);
    }

    public void confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if(!pagamento.isPresent()){
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento.get());
        pedidoClient.alterarStatusParaPago(pagamento.get().getPedidoId());
    }

    public void alterarStatus(Long id){
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if(!pagamento.isPresent()){
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        pagamentoRepository.save(pagamento.get());
    }
}
