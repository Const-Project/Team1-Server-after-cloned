package com.example.const_team1_backend.reaction;

import com.example.const_team1_backend.BaseController;

import com.example.const_team1_backend.common.message.ErrorMessage;
import com.example.const_team1_backend.facility.Facility;
import com.example.const_team1_backend.facility.FacilityService;
import com.example.const_team1_backend.member.Member;
import com.example.const_team1_backend.member.MemberService;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/v1/reactions")
public class ReactionController extends BaseController<Reaction,ReactionService> {

    @Autowired
    private MemberService memberService;

    @Autowired
    private FacilityService facilityService;

    public ReactionController(ReactionService service) {
        super(service);
    }

    @Transactional
    @DeleteMapping("/delete/{facilityId}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Long facilityId, @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException, BadRequestException {
        Member member = memberService.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.INVALID_CREDENTIALS.getMessage()));

        // Optional을 활용한 리액션 조회
        Reaction reaction = service.findByFacilityIdAndMemberId(facilityId, member.getId())
                .orElseThrow(() -> new BadRequestException(ErrorMessage.REACT_NOT_EXIST.getMessage()));

        if (!reaction.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException(ErrorMessage.NOT_AUTHORIZED.getMessage());
        }
        member.deleteReaction(reaction);
        Facility facility = facilityService.findById(facilityId);
        facility.deleteReaction(reaction);
        service.deleteById(reaction.getId());
        return ResponseEntity.ok().build();
    }
}
