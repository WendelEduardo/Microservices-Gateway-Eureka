package br.com.alurafood.pagamentos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDoPedidoDTO {

    private Long id;
    private Integer quantidade;
    private String descricao;
}
