package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        item1 = new Item();
        item1.setId(1L);
        item1.setName("Item1");

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Item2");
    }

    @Test
    public void testGetItems() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(item1));
        assertTrue(response.getBody().contains(item2));
    }

    @Test
    public void testGetItemById_ItemExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item1, response.getBody());
    }

    @Test
    public void testGetItemById_ItemDoesNotExist() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetItemsByName_ItemsExist() {
        when(itemRepository.findByName("Item1")).thenReturn(Arrays.asList(item1));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().contains(item1));
    }

    @Test
    public void testGetItemsByName_ItemsDoNotExist() {
        when(itemRepository.findByName("NonExistingItem")).thenReturn(Arrays.asList());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("NonExistingItem");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
