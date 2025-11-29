package com.grupoagil.proyectoagil.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupoagil.proyectoagil.dto.CocinaPedidoDTO;
import com.grupoagil.proyectoagil.dto.ModificarPedidoDTO;
import com.grupoagil.proyectoagil.dto.PedidoDTO;
import com.grupoagil.proyectoagil.model.DetallePedido;
import com.grupoagil.proyectoagil.model.Inventario;
import com.grupoagil.proyectoagil.model.Mesa;
import com.grupoagil.proyectoagil.model.Pedido;
import com.grupoagil.proyectoagil.model.Producto;
import com.grupoagil.proyectoagil.model.Usuario;
import com.grupoagil.proyectoagil.repository.InventarioRepository;
import com.grupoagil.proyectoagil.repository.MesaRepository;
import com.grupoagil.proyectoagil.repository.PedidoRepository;
import com.grupoagil.proyectoagil.repository.ProductoRepository;
import com.grupoagil.proyectoagil.repository.UsuarioRepository;

@Service
public class PedidoService {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private MesaRepository mesaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private InventarioRepository inventarioRepository;

    @Transactional
    public Pedido crearPedido(PedidoDTO pedidoDTO) {
        Usuario usuario = usuarioRepository.findById(pedidoDTO.getIdUser())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + pedidoDTO.getIdUser()));

        Mesa mesa = mesaRepository.findById(pedidoDTO.getIdMesa())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + pedidoDTO.getIdMesa()));
        
        if (mesa.getIdMesa() != 0) {
            List<String> estadosFinales = Arrays.asList("PAGADO", "CANCELADO");
            List<Pedido> pedidosActivosMesa = pedidoRepository.findByMesaAndEstadoNotIn(mesa, estadosFinales);
            if (!pedidosActivosMesa.isEmpty()) {
                throw new RuntimeException("La mesa " + mesa.getIdMesa() + " ya tiene un pedido activo.");
            }
        }
        
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setMesa(mesa);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("Ordenado");
        pedido.setModificado(false);
        pedido.setTipo(mesa.getIdMesa() == 0 ? "Para Llevar" : "Para Mesa");

        List<DetallePedido> detalles = new ArrayList<>();

        for (PedidoDTO.DetalleRequest detalleDTO : pedidoDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getIdProducto()));

            Inventario inventario = inventarioRepository.findById(producto.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Inventario no encontrado para producto ID: " + producto.getIdProducto()));

            if (inventario.getCantDispo() < detalleDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            inventario.setCantDispo(inventario.getCantDispo() - detalleDTO.getCantidad());
            inventarioRepository.save(inventario);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedidoProducto(pedido, producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setNotas(detalleDTO.getNotas());
            detalle.setEsNuevo(false);
            detalles.add(detalle);
        }

        pedido.setDetalles(detalles);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<CocinaPedidoDTO> obtenerPedidosCocina() {
        List<String> estadosCocina = List.of("Ordenado", "EN_PREPARACION");
        List<Pedido> pedidos = pedidoRepository.findByEstadoIn(estadosCocina);
        return pedidos.stream().map(this::convertirPedidoADTO).collect(Collectors.toList());
    }

    @Transactional
    public Optional<Pedido> actualizarEstado(Long id, String nuevoEstado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setEstado(nuevoEstado);
            pedido.setModificado(false);
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setEsNuevo(false);
            }
            return pedidoRepository.save(pedido);
        });
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosParaPago() {
        return pedidoRepository.findByEstadoIn(List.of("ATENDIDO"));
    }

    @Transactional(readOnly = true)
    public List<CocinaPedidoDTO> obtenerPedidosAtendidos() {
        List<Pedido> pedidos = pedidoRepository.findByEstadoIn(List.of("ATENDIDO"));
        return pedidos.stream().map(this::convertirPedidoADTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double calcularTotalPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + idPedido));
        double subtotal = pedido.getDetalles().stream()
                .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad()).sum();
        return subtotal + (subtotal * 0.18);
    }

    @Transactional(readOnly = true)
    public List<CocinaPedidoDTO> obtenerPedidosOrdenadosMesero(String idUser) {
        Usuario usuario = usuarioRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Pedido> pedidos = pedidoRepository.findByUsuarioAndEstadoInOrderByIdPedidoDesc(usuario, List.of("Ordenado"));
        return pedidos.stream().map(this::convertirPedidoADTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CocinaPedidoDTO> obtenerPedidosModificablesMesero(String idUser) {
        Usuario usuario = usuarioRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Pedido> pedidos = pedidoRepository.findByUsuarioAndEstadoInOrderByIdPedidoDesc(
            usuario, List.of("Ordenado", "EN_PREPARACION", "ATENDIDO"));
        return pedidos.stream().map(this::convertirPedidoADTO).collect(Collectors.toList());
    }

    // ========== MODIFICAR PEDIDO ==========
    @Transactional
    public Pedido modificarPedido(Long idPedido, ModificarPedidoDTO modificacionDTO) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        if (!pedido.getUsuario().getIdUser().equals(modificacionDTO.getIdUser())) {
            throw new RuntimeException("No tienes permiso para modificar este pedido");
        }
        
        List<String> estadosModificables = Arrays.asList("Ordenado", "EN_PREPARACION", "ATENDIDO");
        if (!estadosModificables.contains(pedido.getEstado())) {
            throw new RuntimeException("No se puede modificar un pedido en estado: " + pedido.getEstado());
        }

        String estadoAnterior = pedido.getEstado();
        boolean huboModificacion = false;

        // 1. QUITAR/REDUCIR PRODUCTOS (solo en estado ORDENADO)
        if (modificacionDTO.getProductosQuitar() != null && !modificacionDTO.getProductosQuitar().isEmpty()) {
            if (!"Ordenado".equals(pedido.getEstado())) {
                throw new RuntimeException("Solo se pueden quitar/reducir productos en estado 'Ordenado'. Estado actual: " + pedido.getEstado());
            }

            validarNoEliminaTodosLosProductos(pedido, modificacionDTO.getProductosQuitar());
            
            for (ModificarPedidoDTO.ProductoModificacion prodQuitar : modificacionDTO.getProductosQuitar()) {
                reducirProductoDelPedido(pedido, prodQuitar.getIdProducto(), prodQuitar.getCantidad());
                huboModificacion = true;
            }
        }

        // 2. AGREGAR/AUMENTAR PRODUCTOS (permitido en cualquier estado modificable)
        if (modificacionDTO.getProductosAgregar() != null && !modificacionDTO.getProductosAgregar().isEmpty()) {
            for (ModificarPedidoDTO.ProductoModificacion prodAgregar : modificacionDTO.getProductosAgregar()) {
                aumentarProductoEnPedido(pedido, prodAgregar);
                huboModificacion = true;
            }
        }

        if (huboModificacion) {
            pedido.setModificado(true);
            
            // Si estaba ATENDIDO, cambiar a EN_PREPARACION para que vuelva a cocina
            if ("ATENDIDO".equals(estadoAnterior)) {
                pedido.setEstado("EN_PREPARACION");
            }
            
            pedido.setFecha(LocalDateTime.now());
        }

        return pedidoRepository.save(pedido);
    }

    private void validarNoEliminaTodosLosProductos(
        Pedido pedido,
        List<ModificarPedidoDTO.ProductoModificacion> productosQuitar) {

    // Mapa con las cantidades simuladas por producto
        Map<Long, Integer> cantidadesSimuladas = new HashMap<>();

        // Estado actual del pedido
        for (DetallePedido d : pedido.getDetalles()) {
            Long idProd = d.getProducto().getIdProducto();
            cantidadesSimuladas.put(idProd, d.getCantidad());
        }

        // Aplicar las reducciones de forma simulada
        for (ModificarPedidoDTO.ProductoModificacion pq : productosQuitar) {
            Long idProd = pq.getIdProducto();
            Integer cantAReducir = pq.getCantidad();

            if (cantAReducir == null || cantAReducir <= 0) {
                throw new RuntimeException("La cantidad a reducir debe ser mayor a 0 para el producto ID: " + idProd);
            }

            Integer cantidadActual = cantidadesSimuladas.get(idProd);
            if (cantidadActual == null) {
                throw new RuntimeException("El producto ID " + idProd + " no está en el pedido");
            }

            if (cantAReducir > cantidadActual) {
                throw new RuntimeException(
                    "No se puede reducir más de lo que hay en el pedido para el producto ID " + idProd +
                    ". Actual: " + cantidadActual + ", intentando reducir: " + cantAReducir
                );
            }

            int nuevaCantidad = cantidadActual - cantAReducir;
            if (nuevaCantidad > 0) {
                cantidadesSimuladas.put(idProd, nuevaCantidad);
            } else {
                // Si llega a 0, se elimina el producto de la simulación
                cantidadesSimuladas.remove(idProd);
            }
        }

        // Calcular la cantidad total simulada después de las reducciones
        int totalSimulado = cantidadesSimuladas.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalSimulado <= 0) {
            throw new RuntimeException(
                "El pedido debe conservar al menos un producto con cantidad mayor a 0. " +
                "Si deseas quitar todo, cancela el pedido en lugar de modificarlo."
            );
        }
    }

    // ========== REDUCIR cantidad de producto (o eliminar si llega a 0) ==========
    private void reducirProductoDelPedido(Pedido pedido, Long idProducto, Integer cantidadAReducir) {
        DetallePedido detalle = pedido.getDetalles().stream()
                .filter(d -> d.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto ID " + idProducto + " no está en este pedido"));

        Integer cantidadActual = detalle.getCantidad();
        
        if (cantidadAReducir <= 0) {
            throw new RuntimeException("La cantidad a reducir debe ser mayor a 0");
        }
        
        if (cantidadAReducir > cantidadActual) {
            throw new RuntimeException("No se puede reducir más de lo que existe. Actual: " + cantidadActual + ", Intentando reducir: " + cantidadAReducir);
        }

        // Devolver stock al inventario
        Inventario inventario = inventarioRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para producto ID: " + idProducto));
        inventario.setCantDispo(inventario.getCantDispo() + cantidadAReducir);
        inventarioRepository.save(inventario);

        // Si se reduce toda la cantidad, eliminar el detalle
        if (cantidadAReducir.equals(cantidadActual)) {
            pedido.getDetalles().remove(detalle);
        } else {
            // Si se reduce parcialmente, actualizar la cantidad
            detalle.setCantidad(cantidadActual - cantidadAReducir);
        }
    }

    // ========== AUMENTAR cantidad de producto (o agregar si no existe) ==========
    private void aumentarProductoEnPedido(Pedido pedido, ModificarPedidoDTO.ProductoModificacion prodAgregar) {
        Producto producto = productoRepository.findById(prodAgregar.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + prodAgregar.getIdProducto()));

        Inventario inventario = inventarioRepository.findById(producto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para producto ID: " + producto.getIdProducto()));

        if (inventario.getCantDispo() < prodAgregar.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para: " + producto.getNombre() + ". Disponible: " + inventario.getCantDispo());
        }

        // Descontar del inventario
        inventario.setCantDispo(inventario.getCantDispo() - prodAgregar.getCantidad());
        inventarioRepository.save(inventario);

        // Buscar si el producto ya existe en el pedido
        Optional<DetallePedido> detalleExistente = pedido.getDetalles().stream()
                .filter(d -> d.getProducto().getIdProducto().equals(prodAgregar.getIdProducto()))
                .findFirst();

        if (detalleExistente.isPresent()) {
            DetallePedido detalle = detalleExistente.get();
            detalle.setCantidad(detalle.getCantidad() + prodAgregar.getCantidad());
            detalle.setEsNuevo(true); // Marcar como modificado
            if (prodAgregar.getNotas() != null && !prodAgregar.getNotas().trim().isEmpty()) {
                detalle.setNotas(prodAgregar.getNotas());
            }
        } else {
            DetallePedido nuevoDetalle = new DetallePedido();
            nuevoDetalle.setPedidoProducto(pedido, producto);
            nuevoDetalle.setCantidad(prodAgregar.getCantidad());
            nuevoDetalle.setPrecioUnitario(producto.getPrecio());
            nuevoDetalle.setNotas(prodAgregar.getNotas());
            nuevoDetalle.setEsNuevo(true); // Marcar como nuevo
            pedido.getDetalles().add(nuevoDetalle);
        }
    }

    @Transactional
    public Pedido cancelarPedido(Long idPedido, String idUser) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        if (!pedido.getUsuario().getIdUser().equals(idUser)) {
            throw new RuntimeException("No tienes permiso para cancelar este pedido");
        }
        
        if (!"Ordenado".equals(pedido.getEstado())) {
            throw new RuntimeException("Solo se pueden cancelar pedidos en estado 'Ordenado'");
        }
        
        for (DetallePedido detalle : pedido.getDetalles()) {
            Inventario inventario = inventarioRepository.findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
            inventario.setCantDispo(inventario.getCantDispo() + detalle.getCantidad());
            inventarioRepository.save(inventario);
        }
        
        pedido.setEstado("CANCELADO");
        return pedidoRepository.save(pedido);
    }

    private CocinaPedidoDTO convertirPedidoADTO(Pedido pedido) {
        List<CocinaPedidoDTO.CocinaDetalleDTO> detallesDTO = pedido.getDetalles().stream()
                .map(detalle -> new CocinaPedidoDTO.CocinaDetalleDTO(
                        detalle.getProducto() != null ? detalle.getProducto().getIdProducto() : null,
                        detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto Desconocido",
                        detalle.getCantidad(),
                        detalle.getNotas(),
                        detalle.getEsNuevo() != null ? detalle.getEsNuevo() : false
                ))
                .collect(Collectors.toList());

        String tipoMesa;
        if (pedido.getMesa() != null) {
            tipoMesa = (pedido.getMesa().getIdMesa() == 0) ? "Para Llevar" : "Mesa " + pedido.getMesa().getIdMesa();
        } else {
            tipoMesa = "Mesa Desconocida";
        }

        return new CocinaPedidoDTO(
                pedido.getIdPedido(),
                pedido.getEstado(),
                tipoMesa,
                pedido.getModificado() != null ? pedido.getModificado() : false,
                detallesDTO
        );
    }
}