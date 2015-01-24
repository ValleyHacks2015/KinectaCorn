
// Skeletons.java
// Based off Andrew Davison's open source code


import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.color.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

import org.OpenNI.*;

import java.nio.ShortBuffer;



public class Skeletons
{
  private static final String HEAD_FNM = "gorilla.png";

  // used to colour a user's limbs so they're different from the user's body color 
  private Color USER_COLORS[] = {
    Color.RED, Color.BLUE, Color.CYAN, Color.GREEN,
    Color.MAGENTA, Color.PINK, Color.YELLOW, Color.WHITE};
       // same user colors as in TrackersPanel


  private BufferedImage headImage;    // image that's drawn over the head joint
  
  // OpenNI
  private UserGenerator userGen;
  private DepthGenerator depthGen;

  // OpenNI capabilities used by UserGenerator
  private SkeletonCapability skelCap;
                // to output skeletal data, including the location of the joints
  private PoseDetectionCapability poseDetectionCap;
               // to recognize when the user is in a specific position


  private String calibPoseName = null;

  private HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> userSkels;
    /* userSkels maps user IDs --> a joints map (i.e. a skeleton)
       skeleton maps joints --> positions (was positions + orientations)
    */

  // gesture detectors  (NEW)
  private GestureSequences gestSeqs;
  private SkeletonsGestures skelsGests;
  
  public ArrayList<PatchMap> userPatches = new ArrayList<PatchMap>();


  public Skeletons(UserGenerator userGen, DepthGenerator depthGen, GesturesWatcher watcher)
  {
    this.userGen = userGen;
    this.depthGen = depthGen;

    headImage = loadImage(HEAD_FNM);
    configure();
    userSkels = new HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>>();

    // create the two gesture detectors, and tell them who to notify (NEW)
    gestSeqs = new GestureSequences(watcher);
    skelsGests = new SkeletonsGestures(watcher, userSkels, gestSeqs);
  } // end of Skeletons()


  private BufferedImage loadImage(String fnm)
  // load the image from fnm
  {
    BufferedImage im = null;
    try {
      im = ImageIO.read( new File(fnm));   
      System.out.println("Loaded image from " + fnm); 
    }
    catch (Exception e) 
    { System.out.println("Unable to load image from " + fnm);  }   

    return im;
  }  // end of loadImage()



  private void configure()
  /* create pose and skeleton detection capabilities for the user generator, 
     and set up observers (listeners)   */
  {
    try {
      // setup UserGenerator pose and skeleton detection capabilities;
      // should really check these using ProductionNode.isCapabilitySupported()
      poseDetectionCap = userGen.getPoseDetectionCapability();

      skelCap = userGen.getSkeletonCapability();
      calibPoseName = skelCap.getSkeletonCalibrationPose();  // the 'psi' pose
      skelCap.setSkeletonProfile(SkeletonProfile.ALL);
             // other possible values: UPPER_BODY, LOWER_BODY, HEAD_HANDS

      // set up four observers
      userGen.getNewUserEvent().addObserver(new NewUserObserver());   // new user found
      userGen.getLostUserEvent().addObserver(new LostUserObserver()); // lost a user

      poseDetectionCap.getPoseDetectedEvent().addObserver(
                                             new PoseDetectedObserver());  
          // for when a pose is detected

      skelCap.getCalibrationCompleteEvent().addObserver(
                                             new CalibrationCompleteObserver());
         // for when skeleton calibration is completed, and tracking starts
    } 
    catch (Exception e) {
      System.out.println(e);
      System.exit(1);
    }
  }  // end of configure()


  // --------------- updating ----------------------------

  public void update()
  // update skeleton of each user
  {
    try {   
      int[] userIDs = userGen.getUsers();   // there may be many users in the scene
      for (int i = 0; i < userIDs.length; ++i) {
        int userID = userIDs[i];
        if (skelCap.isSkeletonCalibrating(userID))
          continue;    // test to avoid occassional crashes with isSkeletonTracking()
        if (skelCap.isSkeletonTracking(userID)) {
          updateJoints(userID);

          // when a skeleton changes, have the detectors look for gesture start/finish
          gestSeqs.checkSeqs(userID);    // NEW
          skelsGests.checkGests(userID);
        }
      }
    }
    catch (StatusException e) 
    {  System.out.println(e); }
  }  // end of update()




  private void updateJoints(int userID)
  // update all the joints for this userID in userSkels
  {
    HashMap<SkeletonJoint, SkeletonJointPosition> skel = userSkels.get(userID);

    updateJoint(skel, userID, SkeletonJoint.HEAD);
    updateJoint(skel, userID, SkeletonJoint.NECK);

    updateJoint(skel, userID, SkeletonJoint.LEFT_SHOULDER);
    updateJoint(skel, userID, SkeletonJoint.LEFT_ELBOW);
    updateJoint(skel, userID, SkeletonJoint.LEFT_HAND);

    updateJoint(skel, userID, SkeletonJoint.RIGHT_SHOULDER);
    updateJoint(skel, userID, SkeletonJoint.RIGHT_ELBOW);
    updateJoint(skel, userID, SkeletonJoint.RIGHT_HAND);

    updateJoint(skel, userID, SkeletonJoint.TORSO);

    updateJoint(skel, userID, SkeletonJoint.LEFT_HIP);
    updateJoint(skel, userID, SkeletonJoint.LEFT_KNEE);
    updateJoint(skel, userID, SkeletonJoint.LEFT_FOOT);

    updateJoint(skel, userID, SkeletonJoint.RIGHT_HIP);
    updateJoint(skel, userID, SkeletonJoint.RIGHT_KNEE);
    updateJoint(skel, userID, SkeletonJoint.RIGHT_FOOT);
  }  // end of updateJoints()



  private void updateJoint(HashMap<SkeletonJoint, SkeletonJointPosition> skel,
                            int userID, SkeletonJoint joint)
  /* update the position of the specified user's joint by 
     looking at the skeleton capability
  */
  {
    try {
      // report unavailable joints (should not happen)
      if (!skelCap.isJointAvailable(joint) || !skelCap.isJointActive(joint)) {
        System.out.println(joint + " not available for updates");
        return;
      }

      SkeletonJointPosition pos = skelCap.getSkeletonJointPosition(userID, joint);
      if (pos == null) {
        System.out.println("No update for " + joint);
        return;
      }
      
      SkeletonJointPosition jPos = null;
      if (pos.getPosition().getZ() != 0)   // has a depth position
        jPos = new SkeletonJointPosition( 
                           depthGen.convertRealWorldToProjective(pos.getPosition()),
                                            pos.getConfidence());
      else  // no info found for that user's joint
        jPos = new SkeletonJointPosition(new Point3D(), 0);
      skel.put(joint, jPos);
    }
    catch (StatusException e) 
    {  System.out.println(e); }
  }  // end of updateJoint()



  // -------------------- drawing --------------------------------


  public void draw(Graphics2D g2d)
  // draw skeleton of each user, with a head image, and user status
  {
    g2d.setStroke(new BasicStroke(8));

    try {   
      int[] userIDs = userGen.getUsers();
      for (int i = 0; i < userIDs.length; ++i) {
        setLimbColor(g2d, userIDs[i]);
        if (skelCap.isSkeletonCalibrating(userIDs[i])) 
          {}  // test to avoid occassional crashes with isSkeletonTracking()
        else if (skelCap.isSkeletonTracking(userIDs[i])) {  
          HashMap<SkeletonJoint, SkeletonJointPosition> skel = 
                                              userSkels.get(userIDs[i]);
          drawSkeleton(g2d, skel);
          drawHead(g2d, skel);
        }
        drawUserStatus(g2d, userIDs[i]);
      }
    }
    catch (StatusException e) 
    {  System.out.println(e); }
  }  // end of draw()



  private void setLimbColor(Graphics2D g2d, int userID)
  /* use the 'opposite' of the user ID color for the limbs, so they
     stand out against the colored body */
  {
    Color c = USER_COLORS[userID % USER_COLORS.length];
    Color oppColor = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
    g2d.setColor(oppColor);
  }  // end of setLimbColor()



  private void drawSkeleton(Graphics2D g2d, 
                    HashMap<SkeletonJoint, SkeletonJointPosition> skel) 
  // draw skeleton as lines (limbs) between its joints;
  // hardwired to avoid non-implemented joints
  {
    drawLine(g2d, skel, SkeletonJoint.HEAD, SkeletonJoint.NECK);

    drawLine(g2d, skel, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.TORSO);
    drawLine(g2d, skel, SkeletonJoint.RIGHT_SHOULDER, SkeletonJoint.TORSO);

    drawLine(g2d, skel, SkeletonJoint.NECK, SkeletonJoint.LEFT_SHOULDER);
    drawLine(g2d, skel, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.LEFT_ELBOW);
    drawLine(g2d, skel, SkeletonJoint.LEFT_ELBOW, SkeletonJoint.LEFT_HAND);

    drawLine(g2d, skel, SkeletonJoint.NECK, SkeletonJoint.RIGHT_SHOULDER);
    drawLine(g2d, skel, SkeletonJoint.RIGHT_SHOULDER, SkeletonJoint.RIGHT_ELBOW);
    drawLine(g2d, skel, SkeletonJoint.RIGHT_ELBOW, SkeletonJoint.RIGHT_HAND);

    drawLine(g2d, skel, SkeletonJoint.LEFT_HIP, SkeletonJoint.TORSO);
    drawLine(g2d, skel, SkeletonJoint.RIGHT_HIP, SkeletonJoint.TORSO);
    drawLine(g2d, skel, SkeletonJoint.LEFT_HIP, SkeletonJoint.RIGHT_HIP);

    drawLine(g2d, skel, SkeletonJoint.LEFT_HIP, SkeletonJoint.LEFT_KNEE);
    drawLine(g2d, skel, SkeletonJoint.LEFT_KNEE, SkeletonJoint.LEFT_FOOT);

    drawLine(g2d, skel, SkeletonJoint.RIGHT_HIP, SkeletonJoint.RIGHT_KNEE);
    drawLine(g2d, skel, SkeletonJoint.RIGHT_KNEE, SkeletonJoint.RIGHT_FOOT);
  }  // end of drawSkeleton()



  private void drawLine(Graphics2D g2d, 
             HashMap<SkeletonJoint, SkeletonJointPosition> skel, 
                              SkeletonJoint j1, SkeletonJoint j2)
  // draw a line (limb) between the two joints (if they have positions)
  {
    Point3D p1 = getJointPos(skel, j1);
    Point3D p2 = getJointPos(skel, j2);
    if ((p1 != null) && (p2 != null))
      g2d.drawLine((int) p1.getX(), (int) p1.getY(), 
                           (int) p2.getX(), (int) p2.getY());
  }  // end of drawLine()



  private Point3D getJointPos(HashMap<SkeletonJoint, SkeletonJointPosition> skel, 
                                                SkeletonJoint j)
  // get the (x, y, z) coordinate for the joint (or return null)
  {
    SkeletonJointPosition pos = skel.get(j);
    if (pos == null)
      return null;

    if (pos.getConfidence() == 0)
      return null;   // don't draw a line to a joint with a zero-confidence pos

    return pos.getPosition();
  }  // end of getJointPos()




  private void drawHead(Graphics2D g2d, 
                    HashMap<SkeletonJoint, SkeletonJointPosition> skel) 
  // draw a head image rotated around the z-axis to follow the neck-->head line
  {
    if (headImage == null)
      return;

    Point3D headPt = getJointPos(skel, SkeletonJoint.HEAD);
    Point3D neckPt = getJointPos(skel, SkeletonJoint.NECK);
    if ((headPt == null) || (neckPt == null))
      return;  
    else {
      int angle = 90 - ((int) Math.round( Math.toDegrees(
                            Math.atan2(neckPt.getY()-headPt.getY(), 
                                       headPt.getX()- neckPt.getX()) )));
      // System.out.println("Head image rotated from vertical by " + angle + " degrees");
      drawRotatedHead(g2d, headPt, headImage, angle);
    }
  }  // end of drawHead()



  private void drawRotatedHead(Graphics2D g2d, Point3D headPt,
                                BufferedImage headImage, int angle)
  {
    AffineTransform origTF = g2d.getTransform();    // store original orientation
    AffineTransform newTF = (AffineTransform)(origTF.clone());

    // center of rotation is the head joint
    newTF.rotate( Math.toRadians(angle), (int)headPt.getX(), (int)headPt.getY());
    g2d.setTransform(newTF);

    // draw image centered at head joint
    int x = (int)headPt.getX() - (headImage.getWidth()/2);
    int y = (int)headPt.getY() - (headImage.getHeight()/2);
    g2d.drawImage(headImage, x, y, null);

    g2d.setTransform(origTF);    // reset original orientation
  }  // end of drawRotatedHead()



  private void drawUserStatus(Graphics2D g2d, int userID) throws StatusException
  // draw user ID and status on the skeleton at its center of mass (CoM)
  {
    Point3D massCenter = depthGen.convertRealWorldToProjective(
                                                userGen.getUserCoM(userID));
    String label = null;
    if (skelCap.isSkeletonTracking(userID))     // tracking
      label = new String("Tracking user " + userID);
    else if (skelCap.isSkeletonCalibrating(userID))  // calibrating
      label = new String("Calibrating user " + userID);
    else    // pose detection
      label = new String("Looking for " + calibPoseName + " pose for user " + userID);

    g2d.drawString(label, (int) massCenter.getX(), (int) massCenter.getY());
  }  // end of drawUserStatus()



  // --------------------- 4 observers -----------------------
  /*   user detection --> pose detection --> skeleton calibration -->
       skeleton tracking (and creation of userSkels entry)
       + may also lose a user (and so delete its userSkels entry)

       ===== Changes (December 2011) =============
         LostUserObserver and CalibrationCompleteObserver update the 
         gesture detectors
  */


  class NewUserObserver implements IObserver<UserEventArgs>
  {
    public void update(IObservable<UserEventArgs> observable, UserEventArgs args)
    {
      System.out.println("Detected new user " + args.getId());
    //add new patch mapper for user
      
      try {
        // try to detect a pose for the new user
        poseDetectionCap.StartPoseDetection(calibPoseName, args.getId());// big-S ?
        
        
      }
      catch (StatusException e)
      { e.printStackTrace(); }
    }
  }  // end of NewUserObserver inner class



  class LostUserObserver implements IObserver<UserEventArgs>
  {
    public void update(IObservable<UserEventArgs> observable, UserEventArgs args)
    { 
      int userID = args.getId();
      System.out.println("Lost track of user " + userID);
      //remove the pathMapping for user
      // remove user from the gesture detectors (NEW)
      userSkels.remove(userID);    
      gestSeqs.removeUser(userID);
    }
  } // end of LostUserObserver inner class



  class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs>
  {
    public void update(IObservable<PoseDetectionEventArgs> observable,
                                                     PoseDetectionEventArgs args)
    {
      int userID = args.getUser();
      System.out.println(args.getPose() + " pose detected for user " + userID);
      try {
        // finished pose detection; switch to skeleton calibration
        poseDetectionCap.StopPoseDetection(userID);    // big-S ?
        skelCap.requestSkeletonCalibration(userID, true);
      }
      catch (StatusException e)
      {  e.printStackTrace(); }
    }
  }  // end of PoseDetectedObserver inner class



  class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs>
  {
    public void update(IObservable<CalibrationProgressEventArgs> observable,
                                                    CalibrationProgressEventArgs args)
    {
      int userID = args.getUser();
      System.out.println("Calibration status: " + args.getStatus() + 
                                                    " for user " + userID);
      try {
        if (args.getStatus() == CalibrationProgressStatus.OK) {
          // calibration succeeeded; move to skeleton tracking
          System.out.println("Starting tracking user " + userID);
          skelCap.startTracking(userID);
          userPatches.add(userID - 1, new PatchMap(userID));//TODO make sure this works!
          // add user to the gesture detectors (NEW)
          userSkels.put(new Integer(userID),
                     new HashMap<SkeletonJoint, SkeletonJointPosition>());  
              // create new skeleton map for the user
          gestSeqs.addUser(userID);
        }
        else    // calibration failed; return to pose detection
          poseDetectionCap.StartPoseDetection(calibPoseName, userID);    // big-S ?
      }
      catch (StatusException e)
      {  e.printStackTrace(); }
    }
  }  // end of CalibrationCompleteObserver inner class


} // end of Skeletons class

