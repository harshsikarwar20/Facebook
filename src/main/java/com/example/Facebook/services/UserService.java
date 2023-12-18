package com.example.Facebook.services;

import com.example.Facebook.dto.SignInInput;
import com.example.Facebook.dto.SignInOutput;
import com.example.Facebook.dto.SignUpOutput;
import com.example.Facebook.models.AuthenticationToken;
import com.example.Facebook.models.PostLike;
import com.example.Facebook.models.User;
import com.example.Facebook.repository.ITokenRepository;
import com.example.Facebook.repository.IUserRepository;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    public IUserRepository userRepository;

    @Autowired
    public TokenService tokenService;

    @Autowired
    public ITokenRepository tokenRepository;

    @Autowired
    FollowingService followingService;

    @Autowired
    FollowerService followerService;

    @Autowired
    LikeService likeService;
    public SignUpOutput signUp(User signUpDto) {
        // Check whether this email exits in the database or not

        User user = userRepository.findFirstByEmail(signUpDto.getEmail());

        if(user!=null){
            throw new IllegalStateException("Facebook user already exists!! please signIn instead...");
        }
        // Now encrypt the password...
        String encryptedPassword = null;

        try{
            encryptedPassword = encryptPassword(signUpDto.getPassword());
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        signUpDto.setPassword(encryptedPassword);
        userRepository.save(signUpDto);

        return new SignUpOutput("Facebook user registered","Facebook account created successfully");
    }

    private String encryptPassword(String userPassword)throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        md5.update(userPassword.getBytes());
        byte[] digested = md5.digest();

        String hash = DatatypeConverter.printHexBinary(digested);

        return hash;
    }
    public SignInOutput signIn(SignInInput signInDto) {
        //check if user exists or not based on email
        User user = userRepository.findFirstByEmail(signInDto.getEmail());
        if(user == null) {
            throw new IllegalStateException("User invalid!!!!...sign up instead");
        }

        String encryptedPassword = null;

        try {
            encryptedPassword = encryptPassword(signInDto.getPassword());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //match it with database encrypted password

        boolean isPasswordValid = encryptedPassword.equals(user.getPassword());

        if(!isPasswordValid) {
            throw new IllegalStateException("User invalid!!!!...sign up instead");
        }

        AuthenticationToken token = new AuthenticationToken(user);

        tokenService.saveToken(token);

        //set up output response

        return new SignInOutput("Authentication Successfull!!!", token.getToken());
    }

    public void updateUser(User user, String token) {
        User originalUser = tokenRepository.findFirstByToken(token).getUser();

        if(!(user.getFirstName().isEmpty())){
            originalUser.setFirstName(user.getFirstName());
        }

//        if(user.getLastName()!=null){
        if(!(user.getLastName().isEmpty())){
            originalUser.setLastName(user.getLastName());
        }

        if(user.getPassword()!=null){
            String encryptedPassword = null;

            try{
                encryptedPassword = encryptPassword(user.getPassword());
            }catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }
            originalUser.setPassword(encryptedPassword);
        }

        if((user.getPhoneNumber()!=null)){
            Pattern p = Pattern.compile("\\d{2}-\\d{10}");

            Matcher m = p.matcher(user.getPhoneNumber());
            if( (m.find() && m.group().equals(user.getPhoneNumber()))){
                originalUser.setPhoneNumber(user.getPhoneNumber());
            }else{
                throw new IllegalStateException("Enter correct details");
            }
        }

        if(user.getEmail()!=null){
            Pattern p = Pattern.compile("[a-z0-9._%+-]+@[a-z0-9.-]+\\\\.[a-z]{2,3}");
            Matcher m = p.matcher(user.getEmail());

            if(m.find() && m.group().equals(user.getEmail())){
                originalUser.setEmail(user.getEmail());
            }else{
                throw new IllegalStateException("Enter correct details");
            }
        }
        userRepository.save(originalUser);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }


    public String deleteById(Long userId) {
        userRepository.deleteById(userId);
        return "User deleted successfully";
    }

    public String toggleBlueTick(Long id, boolean blueTick) {
        User user = userRepository.findByUserId(id);

        if(user!=null){
            user.setBlueTicked(blueTick);
            userRepository.save(user);
            return "Blue Tick was set to..." + blueTick;
        }else{
            return "Invalid User...";
        }
    }
    @Transactional
    public String followUser(Long userId, Long otherId) {

        if(userId == otherId) {
            return "Cant follow yourself!!!!";
        }
        User myUser = userRepository.findByUserId(userId);
        User otherUser = userRepository.findByUserId(otherId);

        if(myUser!=null && otherUser!=null) {
            //todo : check if already follows or not
            //follow from my side
            followingService.saveFollowing(myUser,otherUser);
            //follower from other side
            followerService.saveFollower(otherUser, myUser);
            return "Followed Successfully!!!!!";
        }
        else {
            return "Users are invalid!!!";
        }
    }

    public void like(PostLike postLike) {
        likeService.like(postLike);
    }
}
