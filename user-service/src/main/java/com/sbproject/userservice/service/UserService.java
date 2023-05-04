package com.sbproject.userservice.service;

import com.sbproject.userservice.entity.User;
import com.sbproject.userservice.feignclients.BikeFeignClient;
import com.sbproject.userservice.feignclients.CarFeignClient;
import com.sbproject.userservice.model.Car;
import com.sbproject.userservice.model.Bike;
import com.sbproject.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CarFeignClient carFeignClient;
    @Autowired
    BikeFeignClient bikeFeignClient;

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User getUserById(int id){
        return userRepository.findById(id).orElse(null);
    }

    public User save(User user){
        User newUser = userRepository.save(user);
        return newUser;
    }

    public List<Car> getCars(int userId){
        List<Car> cars = restTemplate.getForObject("http://localhost:8002/car/byuser/" + userId, List.class);
        return cars;
    }
    public List<Bike> getBikes(int userId){
        List<Bike> bikes = restTemplate.getForObject("http://localhost:8003/bike/byuser/" + userId, List.class);
        return bikes;
    }

    public Car saveCar(int userId, Car car){
        car.setUserId(userId);
        Car newCar = carFeignClient.save(car);
        return newCar;
    }
    public Bike saveBike(int userId, Bike bike){
        bike.setUserId(userId);
        Bike newBike = bikeFeignClient.save(bike);
        return newBike;
    }

    public Map<String, Object> getUserAndVehicles(int userId){
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findById(userId).orElse(null);
        if(user==null){
            result.put("Message", "user doesn't exist");
            return result;
        }
        result.put("User", user);

        List<Car> cars = carFeignClient.getCars(userId);
        if(cars.isEmpty())
            result.put("Cars", "This user have not cars");
        else
            result.put("Cars", cars);

        List<Bike> bikes = bikeFeignClient.getBikes(userId);
        if(bikes.isEmpty())
            result.put("Bikes", "This user have not bikes");
        else
            result.put("Bikes", bikes);

        return result;
    }

}
