package com.boot;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsSame;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.boot.controller.ShipwreckController;
import com.boot.model.Shipwreck;
import com.boot.repository.ShipwreckRepository;

public class ShipwreckControllerTest {
	@InjectMocks
	private ShipwreckController sc;
	
	@Mock
	private ShipwreckRepository shipwreckRepository;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testShipwreckGet() {
		Shipwreck sw = new Shipwreck();
		sw.setId(1L);
		when(shipwreckRepository.findOne(1L)).thenReturn(sw);
		
		Shipwreck wreck = sc.get(1L);
//		Shipwreck wreck2 = sc.get(1L); //verify will fail
		
		verify(shipwreckRepository).findOne(1L);
		
//		assertEquals("Got correct ship", 1L, wreck.getId().longValue());	
		assertThat(wreck.getId(), is(1L));
	}

	@Test
	public void testShipwreckList() {
		Shipwreck sw = new Shipwreck();
		Shipwreck sw2 = new Shipwreck();
		sw.setId(1L);
		sw2.setId(2L);
		List<Shipwreck> list = new ArrayList<>();
		list.add(sw);
		list.add(sw2);
		when(shipwreckRepository.findAll()).thenReturn(list);
		
		List<Shipwreck> result = sc.list();
		
		verify(shipwreckRepository).findAll();
		
//		assertEquals("Got correct ship", list, result);	
		assertThat("Got correct ships", result, hasItems(sw, sw2));
		assertThat("Got correct size", result.size(), is(2));
	}

	@Test
	public void testShipwreckCreate() {
		Shipwreck sw = new Shipwreck();
		sw.setId(1L);

		when(shipwreckRepository.saveAndFlush(sw)).thenReturn(sw);
		
		Shipwreck result = sc.create(sw);
		
		verify(shipwreckRepository).saveAndFlush(sw);
		
//		assertEquals("Added correct ship", sw, result);	
		assertThat("Added correct ship", result, sameInstance(sw));
	}

	@Test
	public void testShipwreckUpdate() {
		Shipwreck sw = new Shipwreck();
		sw.setId(1L);

		when(shipwreckRepository.findOne(1L)).thenReturn(sw);
		when(shipwreckRepository.saveAndFlush(sw)).thenReturn(sw);
		
		Shipwreck result = sc.update(1L, sw);
		
		verify(shipwreckRepository).findOne(1L);
		verify(shipwreckRepository).saveAndFlush(sw);
		
//		assertEquals("Updated correct ship", sw, result);	
		assertThat("Updated correct ship", result, sameInstance(sw));
	}

	@Test
	public void testShipwreckDelete() {
		Shipwreck sw = new Shipwreck();
		sw.setId(1L);

		when(shipwreckRepository.findOne(1L)).thenReturn(sw);
		
		Shipwreck result = sc.delete(1L);
		
		verify(shipwreckRepository).findOne(1L);
		verify(shipwreckRepository).delete(sw);
		
//		assertEquals("Deleted correct ship", sw, result);	
		assertThat("Deleted correct ship", result, sameInstance(sw));
	}

	
	
}
