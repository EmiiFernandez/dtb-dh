package dropthebass.equipo4.service;


import dropthebass.equipo4.dto.ShoppingCartDTO;
import dropthebass.equipo4.exeptions.ResourceNotFoundException;

import java.util.List;

public interface IShoppingCartService {
    public List<ShoppingCartDTO> getListByUser() throws ResourceNotFoundException;
    public void cleanShoppingCart(String userEmail) throws ResourceNotFoundException;

    public void removeProduct(Long id) throws ResourceNotFoundException;
    public void addProduct(ShoppingCartDTO shoppingCartDTO) throws ResourceNotFoundException;
    public Long getCountByUser(String userEmail) throws ResourceNotFoundException;

    }
