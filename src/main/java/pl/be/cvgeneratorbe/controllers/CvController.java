package pl.be.cvgeneratorbe.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;
import pl.be.cvgeneratorbe.dto.DataBaseCV;
import pl.be.cvgeneratorbe.dto.UserCV;
import pl.be.cvgeneratorbe.service.CvService;

import java.util.List;


@RestController
@RequestMapping("cv")
public class CvController {
    private final CvService cvService;

    public CvController(CvService cvService) {
        this.cvService = cvService;
    }

    @GetMapping("/resume")
    public ResponseEntity<UserCV> parseLinkedInResume(@RequestParam("file")MultipartFile file){
        return ResponseEntity.ok(cvService.parseFromResume(file));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserCV> parseLinkedInProfile(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(cvService.parseFromProfile(file));
    }

    @PostMapping
    public DataBaseCV saveCV(@RequestBody UserCV userCv){
        return cvService.saveCV(userCv);
    }

    @GetMapping(path = "/{nameAndSurname}")
    public List<DataBaseCV> getByNameAndSurname(@PathVariable String nameAndSurname){
        return cvService.findByNameAndSurname(nameAndSurname);
    }
}
