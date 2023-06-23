package br.com.alurafood.pagamentos.http;

import br.com.alurafood.pagamentos.dto.PedidoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("pedidos-ms")
public interface PedidoClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/pedidos/{id}/pago")
    void alterarStatusParaPago(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/pedidos/{id}")
    ResponseEntity<PedidoDTO> listarPorId(@PathVariable("id") Long id);

}
