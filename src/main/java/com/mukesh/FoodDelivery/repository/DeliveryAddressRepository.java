package com.mukesh.FoodDelivery.repository;


import com.mukesh.FoodDelivery.model.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

    // லாகின் பண்ணியிருக்க கஸ்டமரோட எல்லா முகவரிகளையும் எடுக்க
    List<DeliveryAddress> findByCustomerId(Long customerId);
}
