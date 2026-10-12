package com.reevil.client.mrx;

import com.reevil.anim.AnimClip;
import com.reevil.entity.mrx.MrXEntity;

import static com.reevil.anim.K.*;

/**
 * Dedicated clips for characters that share the Mr. X rig + move timings (haymaker 30t@.5, jab-hook 28t@.321/.679, choke 72t@.194/.417/.611/.833, stomp 24t@.5).
 * Each character returns its own clip for the inherited move ids, so the same hit frames carry a different fighting style.
 */
public final class SignatureClips {
    private SignatureClips() {}
    private static final AnimClip W_CROSS = wCross(), W_FLURRY = wFlurry(), W_KICK = wKick(), W_THROW = wThrow(),
        H_SLAM = hSlam(), H_SPIN = hSpin(), H_CRUSH = hCrush(), H_STOMP = hStomp(),
        J_CHOP = jChop(), J_LUNGE = jLunge(), J_SLAM = jSlam(),
        V_STAB = vStab(), V_RAKE = vRake();

    public static AnimClip wesker(int id) {
        switch (id) { case MrXEntity.HAYMAKER: return W_CROSS; case MrXEntity.JAB_HOOK: return W_FLURRY; case MrXEntity.STOMP: return W_KICK; case MrXEntity.CHOKE: return W_THROW; default: return null; }
    }
    public static AnimClip heisenberg(int id) {
        switch (id) { case MrXEntity.HAYMAKER: return H_SLAM; case MrXEntity.JAB_HOOK: return H_SPIN; case MrXEntity.CHOKE: return H_CRUSH; case MrXEntity.STOMP: return H_STOMP; default: return null; }
    }
    public static AnimClip jack(int id) {
        switch (id) { case MrXEntity.HAYMAKER: return J_CHOP; case MrXEntity.JAB_HOOK: return J_LUNGE; case MrXEntity.CHOKE: return J_SLAM; default: return null; }
    }
    /** Dimitrescu / Verdugo: rake + stab styles (claw ids 6/7 keep the Ustanak clips). */
    public static AnimClip claws(int id) {
        switch (id) { case MrXEntity.HAYMAKER: return V_RAKE; case MrXEntity.JAB_HOOK: return V_STAB; default: return null; }
    }

    // ---- Wesker: speed
    /** 30t @.5: crouch low, vanish forward into a straight cross, hips snapping through. */
    private static AnimClip wCross() { AnimClip.Builder b = AnimClip.create();
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 4, 2), k(0.45f, 0, 3, -6, SNAP), k(0.5f, 0, 2, -8, SNAP), k(0.8f, 0, 0.5f, -3), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, 22, 30, 0), k(0.5f, 24, -28, 0, SNAP), k(0.8f, 10, -10, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -30, 10, 20), k(0.46f, -60, 0, 5, SNAP), k(0.5f, -88, -4, 0, SNAP), k(0.7f, -50, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.3f, -100, 0, 0), k(0.5f, 0, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -50, 0, -10), k(0.5f, 15, 0, -15), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.3f, -110, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.3f, 35, 0, 0), k(0.5f, 20, 0, 0), k(1, 0, 0, 0)); rot(b, "thighR", k(0, 0, 0, 0), k(0.3f, -40, 0, 0), k(0.5f, -50, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinL", k(0, 0, 0, 0), k(0.3f, 40, 0, 0), k(1, 0, 0, 0)); rot(b, "head", k(0, 0, 0, 0), k(0.3f, 10, 12, 0), k(0.5f, 6, -15, 0), k(1, 0, 0, 0));
        return b.build(); }
    /** 28t jab@.321, cross-hook@.679 with a free left in between: blur of alternating punches, shoulders pumping. */
    private static AnimClip wFlurry() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -40, 10, 10), k(0.321f, -90, -6, 0, SNAP), k(0.42f, -35, 10, 10), k(0.62f, -50, 20, 40), k(0.679f, -80, -40, 60, SNAP), k(0.85f, -20, 0, 10), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.15f, -100, 0, 0), k(0.321f, 0, 0, 0, SNAP), k(0.42f, -100, 0, 0), k(0.679f, -20, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.2f, -45, 0, -10), k(0.5f, -90, 6, 0, SNAP), k(0.6f, -40, 0, -10), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.2f, -105, 0, 0), k(0.5f, 0, 0, 0, SNAP), k(0.6f, -100, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 14, 18, 0), k(0.321f, 14, -22, 0, SNAP), k(0.5f, 14, 24, 0, SNAP), k(0.679f, 18, -34, 0, SNAP), k(0.9f, 4, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.15f, 0, 3, 0), k(0.5f, 0, 3, -3), k(0.679f, 0, 2, -5, SNAP), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.2f, 30, 0, 0), k(0.679f, 25, 0, 0), k(1, 0, 0, 0)); rot(b, "thighR", k(0, 0, 0, 0), k(0.2f, -30, 0, 0), k(0.679f, -35, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.321f, 6, -10, 0), k(0.679f, 8, -20, 0), k(1, 0, 0, 0));
        return b.build(); }
    /** 24t @.5: chamber the knee high, pivot on the standing foot and snap the heel out, arms flung wide for balance. */
    private static AnimClip wKick() { AnimClip.Builder b = AnimClip.create();
        rot(b, "thighR", k(0, 0, 0, 0), k(0.3f, -100, 0, 0), k(0.5f, -92, 0, 0, SNAP), k(0.75f, -40, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinR", k(0, 0, 0, 0), k(0.3f, 125, 0, 0), k(0.5f, 0, 0, 0, SNAP), k(0.75f, 60, 0, 0), k(1, 0, 0, 0));
        rot(b, "footR", k(0, 0, 0, 0), k(0.5f, -35, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.3f, 10, 0, 0), k(0.5f, 14, 0, 0), k(1, 0, 0, 0)); rot(b, "shinL", k(0, 0, 0, 0), k(0.5f, 14, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, 4, 25, 0), k(0.5f, 20, -30, 0, SNAP), k(0.8f, 6, -8, 0), k(1, 0, 0, 0)); rot(b, "base", k(0, 0, 0, 0), k(0.3f, -4, 20, 0), k(0.5f, -14, -30, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -20, 0, -60), k(0.5f, -10, 0, -85), k(1, 0, 0, 0)); rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -10, 0, 70), k(0.5f, 0, 0, 85), k(1, 0, 0, 0));
        return b.build(); }
    /** 72t, hits .194/.417/.611/.833: seize, haul overhead, drive down, flick away - hands never open. */
    private static AnimClip wThrow() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armL", k(0, 0, 0, 0), k(0.15f, -85, 0, -8), k(0.194f, -90, 0, -4, SNAP), k(0.417f, -165, 0, 0), k(0.5f, -170, 0, 0), k(0.611f, -40, 0, 0, SNAP), k(0.833f, -75, 0, -10, SNAP), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -85, 0, 8), k(0.194f, -90, 0, 4, SNAP), k(0.417f, -165, 0, 0), k(0.5f, -170, 0, 0), k(0.611f, -40, 0, 0, SNAP), k(0.833f, -75, 0, 10, SNAP), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.194f, -20, 0, 0), k(0.5f, -40, 0, 0), k(0.611f, -5, 0, 0), k(1, 0, 0, 0)); rot(b, "foreR", k(0, 0, 0, 0), k(0.194f, -20, 0, 0), k(0.5f, -40, 0, 0), k(0.611f, -5, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.194f, 14, 0, 0), k(0.417f, -22, 0, 0), k(0.611f, 38, 0, 0, SNAP), k(0.833f, 10, 24, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.194f, 0, 2, -2), k(0.417f, 0, -1, 2), k(0.611f, 0, 4, -4, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.417f, -16, 0, 0), k(0.611f, 22, 0, 0), k(1, 0, 0, 0));
        return b.build(); }

    // ---- Heisenberg: heavy two-handed hammer work
    /** 30t @.5: both fists hoisted over the head, a full-body drop into the ground. */
    private static AnimClip hSlam() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.4f, -170, 0, 12), k(0.5f, -20, 0, 6, SNAP), k(0.8f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.4f, -170, 0, -12), k(0.5f, -20, 0, -6, SNAP), k(0.8f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.4f, -30, 0, 0), k(0.5f, -4, 0, 0), k(1, 0, 0, 0)); rot(b, "foreL", k(0, 0, 0, 0), k(0.4f, -30, 0, 0), k(0.5f, -4, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.4f, -24, 0, 0), k(0.5f, 42, 0, 0, SNAP), k(0.8f, 26, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.4f, 0, -1, 2), k(0.5f, 0, 5, -4, SNAP), k(0.8f, 0, 2, -2), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.4f, -18, 0, 0), k(0.5f, 20, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.5f, -30, 0, 0), k(1, 0, 0, 0)); rot(b, "thighR", k(0, 0, 0, 0), k(0.5f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinL", k(0, 0, 0, 0), k(0.5f, 40, 0, 0), k(1, 0, 0, 0)); rot(b, "shinR", k(0, 0, 0, 0), k(0.5f, 40, 0, 0), k(1, 0, 0, 0));
        return b.build(); }
    /** 28t @.321/.679: a spinning double backhand - whole body wound up and released twice, arms outstretched like rotor blades. */
    private static AnimClip hSpin() { AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 0, 60, 0), k(0.321f, 0, -70, 0, SNAP), k(0.5f, 0, 80, 0), k(0.679f, 0, -90, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.15f, 0, 35, 0), k(0.321f, 0, -45, 0, SNAP), k(0.5f, 0, 50, 0), k(0.679f, 0, -60, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -10, 0, 90), k(0.321f, -10, 0, 80, SNAP), k(0.5f, -10, 0, 90), k(0.679f, -10, 0, 80, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.15f, -10, 0, -90), k(0.321f, -10, 0, -80, SNAP), k(0.5f, -10, 0, -90), k(0.679f, -10, 0, -80, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.321f, 0, 30, 0), k(0.679f, 0, 40, 0), k(1, 0, 0, 0)); pos(b, "base", k(0, 0, 0, 0), k(0.15f, 0, 2, 0), k(0.679f, 0, 3, -3), k(1, 0, 0, 0));
        return b.build(); }
    /** 72t: seize in a vice, lift, grind - slow shoulders-first crush with the head tucked down. */
    private static AnimClip hCrush() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armL", k(0, 0, 0, 0), k(0.15f, -80, 0, -20), k(0.194f, -85, 0, -35, SNAP), k(0.417f, -95, 0, -45), k(0.611f, -100, 0, -50), k(0.833f, -100, 0, -55, SNAP), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -80, 0, 20), k(0.194f, -85, 0, 35, SNAP), k(0.417f, -95, 0, 45), k(0.611f, -100, 0, 50), k(0.833f, -100, 0, 55, SNAP), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.194f, -50, 0, 0), k(0.833f, -75, 0, 0), k(1, 0, 0, 0)); rot(b, "foreR", k(0, 0, 0, 0), k(0.194f, -50, 0, 0), k(0.833f, -75, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.194f, 20, 0, 0), k(0.417f, 28, 8, 0), k(0.611f, 32, -8, 0), k(0.833f, 36, 8, 0), k(1, 0, 0, 0)); rot(b, "head", k(0, 0, 0, 0), k(0.4f, 24, 0, 0), k(0.9f, 24, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.194f, 0, 3, -3), k(0.833f, 0, 5, -3), k(1, 0, 0, 0));
        return b.build(); }
    /** 24t @.5: knee up high, then a heavy flat-footed stamp that drops his entire frame. */
    private static AnimClip hStomp() { AnimClip.Builder b = AnimClip.create();
        rot(b, "thighR", k(0, 0, 0, 0), k(0.35f, -105, 0, 0), k(0.5f, -45, 0, 0, SNAP), k(0.8f, -10, 0, 0), k(1, 0, 0, 0)); rot(b, "shinR", k(0, 0, 0, 0), k(0.35f, 90, 0, 0), k(0.5f, 10, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.35f, -18, 0, 0), k(0.5f, 24, 0, 0, SNAP), k(0.8f, 8, 0, 0), k(1, 0, 0, 0)); pos(b, "base", k(0, 0, 0, 0), k(0.35f, 0, -2, 0), k(0.5f, 0, 5, -2, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.35f, -60, 0, -30), k(0.5f, 20, 0, -30), k(1, 0, 0, 0)); rot(b, "armR", k(0, 0, 0, 0), k(0.35f, -60, 0, 30), k(0.5f, 20, 0, 30), k(1, 0, 0, 0));
        return b.build(); }

    // ---- Jack Baker: shovel chop, lunging grab, slam
    /** 30t @.5: shovel hauled up over one shoulder, then a chopping diagonal with a lurch of the whole trunk. */
    private static AnimClip jChop() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.4f, -150, -20, 35), k(0.46f, -120, 0, 20), k(0.5f, -15, 25, -10, SNAP), k(0.8f, -5, 10, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.4f, -130, 10, -20), k(0.5f, -30, -10, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.4f, -14, 30, 10), k(0.5f, 36, -34, 0, SNAP), k(0.8f, 18, -10, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.4f, 0, -1, 3), k(0.5f, 0, 3, -5, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.4f, -10, 20, 0), k(0.5f, 18, -20, 0), k(1, 0, 0, 0));
        return b.build(); }
    /** 28t @.321/.679: lurching double lunge - hands grasping, then a wild scooping swipe. */
    private static AnimClip jLunge() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -30, 0, 30), k(0.321f, -85, -12, 5, SNAP), k(0.5f, -35, 0, 20), k(0.679f, -20, -60, 70, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.15f, -30, 0, -30), k(0.321f, -75, 12, -5, SNAP), k(0.5f, -55, 0, -40), k(0.679f, -90, 20, -10, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 10, 0, 6), k(0.321f, 30, 0, -6, SNAP), k(0.5f, 12, 0, 8), k(0.679f, 34, 0, -8, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.321f, 0, 2, -5, SNAP), k(0.5f, 0, 1, -2), k(0.679f, 0, 3, -6, SNAP), k(1, 0, 0, 0)); rot(b, "head", k(0, 0, 0, 0), k(0.321f, 20, 0, 6), k(0.679f, 26, 0, -6), k(1, 0, 0, 0));
        return b.build(); }
    /** 72t: seizes by the throat/shoulders, hoists, then slams repeatedly - each hit a dead drop of the torso. */
    private static AnimClip jSlam() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armL", k(0, 0, 0, 0), k(0.194f, -90, 0, -10, SNAP), k(0.3f, -150, 0, -5), k(0.417f, -30, 0, -5, SNAP), k(0.5f, -150, 0, -5), k(0.611f, -30, 0, -5, SNAP), k(0.72f, -150, 0, -5), k(0.833f, -30, 0, -5, SNAP), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.194f, -90, 0, 10, SNAP), k(0.3f, -150, 0, 5), k(0.417f, -30, 0, 5, SNAP), k(0.5f, -150, 0, 5), k(0.611f, -30, 0, 5, SNAP), k(0.72f, -150, 0, 5), k(0.833f, -30, 0, 5, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.194f, 16, 0, 0), k(0.3f, -20, 0, 0), k(0.417f, 36, 0, 0, SNAP), k(0.5f, -20, 0, 0), k(0.611f, 36, 0, 0, SNAP), k(0.72f, -20, 0, 0), k(0.833f, 40, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.194f, 0, 2, -2), k(0.417f, 0, 4, -3, SNAP), k(0.611f, 0, 4, -3, SNAP), k(0.833f, 0, 5, -3, SNAP), k(1, 0, 0, 0));
        return b.build(); }

    // ---- Dimitrescu / Verdugo: claw styles
    /** 30t @.5: arm raised high over the shoulder, then a raking downward diagonal slash that drags the shoulder after it. */
    private static AnimClip vRake() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.4f, -160, -10, 30), k(0.5f, -10, 30, -20, SNAP), k(0.75f, 0, 10, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.4f, -40, 0, 0), k(0.5f, 0, 0, 0, SNAP), k(1, 0, 0, 0)); rot(b, "handR", k(0, 0, 0, 0), k(0.4f, -30, 0, 0), k(0.5f, 30, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.4f, -16, 36, 8), k(0.5f, 30, -34, -6, SNAP), k(0.8f, 12, -10, 0), k(1, 0, 0, 0)); rot(b, "head", k(0, 0, 0, 0), k(0.4f, -10, 22, 0), k(0.5f, 14, -20, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.4f, 30, 0, -50), k(0.5f, -20, 0, -40), k(1, 0, 0, 0)); pos(b, "base", k(0, 0, 0, 0), k(0.5f, 0, 2, -4, SNAP), k(1, 0, 0, 0));
        return b.build(); }
    /** 28t @.321/.679: two precise piercing jabs of the blades - elbow tucked, wrist driving, then a cross-body slice. */
    private static AnimClip vStab() { AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -40, 20, 20), k(0.321f, -88, -8, 0, SNAP), k(0.5f, -45, 20, 25), k(0.679f, -30, -70, 40, SNAP), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.15f, -95, 0, 0), k(0.321f, 0, 0, 0, SNAP), k(0.5f, -90, 0, 0), k(0.679f, -10, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 6, 28, 0), k(0.321f, 12, -20, 0, SNAP), k(0.5f, 8, 30, 0), k(0.679f, 14, -42, 0, SNAP), k(1, 0, 0, 0)); pos(b, "base", k(0, 0, 0, 0), k(0.321f, 0, 1, -4, SNAP), k(0.679f, 0, 2, -5, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -30, 0, -45), k(0.679f, -20, 0, -55), k(1, 0, 0, 0)); rot(b, "head", k(0, 0, 0, 0), k(0.321f, 4, -14, 0), k(0.679f, 6, -26, 0), k(1, 0, 0, 0));
        return b.build(); }
}
