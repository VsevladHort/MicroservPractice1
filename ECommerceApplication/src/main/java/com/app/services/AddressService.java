package com.app.services;

import java.util.List;

import com.app.payloads.AddressDTO;
import com.app.user.services.Address;

public interface AddressService {
	
	AddressDTO createAddress(AddressDTO addressDTO);
	
	List<AddressDTO> getAddresses();
	
	AddressDTO getAddress(Long addressId);
	
	AddressDTO updateAddress(Long addressId, Address address);
	
	String deleteAddress(Long addressId);
}
