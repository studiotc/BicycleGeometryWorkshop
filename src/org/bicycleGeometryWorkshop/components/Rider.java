/*
 * The MIT License
 *
 * Copyright 2019 Tom.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.bicycleGeometryWorkshop.components;

import org.bicycleGeometryWorkshop.report.ReportField;
import org.bicycleGeometryWorkshop.report.Report;
import org.bicycleGeometryWorkshop.geometry.IntersectionPoint;
import org.bicycleGeometryWorkshop.geometry.IntersectionPointResult;
import org.bicycleGeometryWorkshop.geometry.Utilities;
import static org.bicycleGeometryWorkshop.geometry.Utilities.circleCircleIntersection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;

/**
 * Rider contains all the geometry for a Bicycle specific rider.
 * The rider is a component of the bicycle, but references the project level
 * rider measurements and rider pose.
 *
 * @author Tom
 */
public class Rider extends BaseComponent {

    private Point2D _handPoint;
    private Point2D _rightFootPoint;
    private Point2D _leftFootPoint;

    private Line2D _geomTorso;
    private Line2D _geomUpperArm;
    private Line2D _geomLowerArm;
    private Line2D _geomHand;

    private Line2D _geomUpperLegRight;
    private Line2D _geomLowerLegRight;
    private Line2D _geomAnkleRight;
    private Line2D _geomFootRight;
    private Line2D _geomToeRight;

    private Line2D _geomUpperLegLeft;
    private Line2D _geomLowerLegLeft;
    private Line2D _geomAnkleLeft;
    private Line2D _geomFootLeft;
    private Line2D _geomToeLeft;

    private Ellipse2D _geomHead;

    RiderMeasurements _riderSize;
    RiderPose _riderPose;

    /**
     *Class constructor.
     * @param riderSize  The rider size object for reference.
     * @param riderPose The rider pose object for reference.
     */
    public Rider(RiderMeasurements riderSize, RiderPose riderPose) {
        super(DataBaseKeys.RIDER_GEOM.toString(), null);

        _riderSize = riderSize;

        _riderPose = riderPose;

        //geometry points set from outside
        _handPoint = new Point2D.Double(0, 0);
        _rightFootPoint = new Point2D.Double(0, 0);
        _leftFootPoint = new Point2D.Double(0, 0);

        //torso and arm lines
        _geomTorso = addLine2D();
        _geomUpperArm = addLine2D();
        _geomLowerArm = addLine2D();
        _geomHand = addLine2D();

        //right leg geometry
        _geomUpperLegRight = addLine2D();
        _geomLowerLegRight = addLine2D();
        _geomAnkleRight = addLine2D();
        _geomFootRight = addLine2D();
        _geomToeRight = addLine2D();

        //left leg geometry
        _geomUpperLegLeft = addLine2D();
        _geomLowerLegLeft = addLine2D();
        _geomAnkleLeft = addLine2D();
        _geomFootLeft = addLine2D();
        _geomToeLeft = addLine2D();

        //head ellipse
        _geomHead = addEllipse2D();

       
        
        update();
    }

    /**
     * Update the rider.  This updates the rider from the calculated positions.
     * @param basePoint  The base point is the seated position or out of saddle position.
     * @param rightFoot The right foot location.
     * @param leftFoot The left foot location.
     * @param handPoint The hand location.
     */
    public void updateRider(Point2D basePoint, Point2D rightFoot, Point2D leftFoot, Point2D handPoint) {

        setBasePoint(basePoint);
        _rightFootPoint.setLocation(rightFoot);
        _leftFootPoint.setLocation(leftFoot);
        _handPoint.setLocation(handPoint);

        update();

    }

    @Override
    public void updateGeometry() {

        //base point - sit point
        Point2D basePoint = getBasePoint();

        UpperSolution upperSolution = solveUpper(basePoint, _handPoint);

        _geomTorso.setLine(upperSolution.torso);
        _geomUpperArm.setLine(upperSolution.upperArm);
        _geomLowerArm.setLine(upperSolution.lowerArm);
        _geomHand.setLine(upperSolution.hand);

        //rider needs a head...
        Point2D shoulder = upperSolution.torso.getP2();
        double torsoAngle = upperSolution.torsoAngleRad;

        double hHeight = _riderSize.getHeadHeight();
        double hWidth = _riderSize.getHeadWidth();
        double neckLength = _riderSize.getHeadWidth();

        double hh = hHeight / 2;
        double hw = hWidth / 2;

        Point2D headBase = Utilities.polarPoint(shoulder, neckLength, torsoAngle);

        //_geomNeck.setLine(shoulder, headBase);
        _geomHead.setFrame(headBase.getX() - hw / 2, headBase.getY() - hh, hWidth, hHeight);

        //==================
        //   hip point
        //==================
        double hipOffset = _riderSize.getHipOffset(); //_attrHipOffset.getDoubleValue();

        Point2D hipPoint = Utilities.polarPoint(basePoint, hipOffset, torsoAngle);

        //solve right leg
        LegSolution rLegSol = solveLeg(hipPoint, _rightFootPoint);

        _geomUpperLegRight.setLine(rLegSol.upperLeg);
        _geomLowerLegRight.setLine(rLegSol.lowerLeg);
        _geomAnkleRight.setLine(rLegSol.ankle);
        _geomFootRight.setLine(rLegSol.foot);
        _geomToeRight.setLine(rLegSol.toe);

        //solve left Leg
        LegSolution lLegSol = solveLeg(hipPoint, _leftFootPoint);

        _geomUpperLegLeft.setLine(lLegSol.upperLeg);
        _geomLowerLegLeft.setLine(lLegSol.lowerLeg);
        _geomAnkleLeft.setLine(lLegSol.ankle);
        _geomFootLeft.setLine(lLegSol.foot);
        _geomToeLeft.setLine(lLegSol.toe);

    }

    /**
     * Solve the Kinematics of the leg from the contact point and hip point
     *
     * @param hip The Hip Point
     * @param ballOfFoot The center of the ball of the foot.
     * @return
     */
    private LegSolution solveLeg(Point2D hip, Point2D ballOfFoot) {
        LegSolution solution = new LegSolution();

        double toe = _riderSize.getToe(); //_attrToe.getDoubleValue();
        double foot = _riderSize.getFoot(); //_attrFoot.getDoubleValue();
        double ankle = _riderSize.getAnkleHeight(); //_attrAnkleHeight.getDoubleValue();
        double lLeg = _riderSize.getLowerLeg(); //_attrLowerLeg.getDoubleValue();
        double uLeg = _riderSize.getUpperLeg(); //_attrUpperLeg.getDoubleValue();

        double pedalAngle = Utilities.degreesToRadians(_riderPose.getPedalRotation());
        double toeAngle = Utilities.degreesToRadians(_riderPose.getToeAngle());
        double HPi = Math.PI / 2;

        double ballX = ballOfFoot.getX();
        double ballY = ballOfFoot.getY();

        double hipX = hip.getX();
        double hipY = hip.getY();

        Point2D toePoint = Utilities.polarPoint(ballOfFoot, toe, -pedalAngle);

        //toe line X for display
        solution.toe = new Line2D.Double(ballOfFoot, toePoint);

        //foot and ankle lines
        Point2D footBase = Utilities.polarPoint(ballOfFoot, foot, -pedalAngle + Math.PI - toeAngle);
        Point2D ankleBase = Utilities.polarPoint(footBase, ankle, -pedalAngle + HPi - toeAngle);

        solution.foot = new Line2D.Double(ballOfFoot, footBase);
        solution.ankle = new Line2D.Double(footBase, ankleBase);

        //leg length hip to ankle
        double legLength = lLeg + uLeg;
        //current measure distance
        double ankleLength = hip.distance(ankleBase);

        Point2D kneePoint = new Point2D.Double();

        if (legLength < ankleLength) {
            //can't reach!  Layout at angle from hip...

            double ax = ankleBase.getX();
            double ay = ankleBase.getY();

            double hkAngle = Math.atan2(ay - hipY, ax - hipX);

            double kneeX = hipX + uLeg * Math.cos(hkAngle);
            double kneeY = hipY + uLeg * Math.sin(hkAngle);

            double crAnkleX = kneeX + lLeg * Math.cos(hkAngle);
            double crAnkleY = kneeY + lLeg * Math.sin(hkAngle);

            double crFootX = crAnkleX;
            double crFootY = crAnkleY - ankle;

            double crBallX = crFootX + foot;
            double crBallY = crFootY;

            double crToeX = crBallX + toe;
            double crToeY = crBallY;

            solution.upperLeg = new Line2D.Double(hipX, hipY, kneeX, kneeY);
            solution.lowerLeg = new Line2D.Double(kneeX, kneeY, crAnkleX, crAnkleY);
            solution.ankle = new Line2D.Double(crAnkleX, crAnkleY, crFootX, crFootY);
            solution.foot = new Line2D.Double(crFootX, crFootY, crBallX, crBallY);
            solution.toe = new Line2D.Double(crFootX, crFootY, crToeX, crToeY);

        } else {

            //foot within reach of pedal
            IntersectionPoint intPnt = circleCircleIntersection(hip, uLeg, ankleBase, lLeg);

            if (intPnt.result() == IntersectionPointResult.TWO_POINTS) {

                Point2D intPnt1 = intPnt.getIntersection1();
                Point2D intPnt2 = intPnt.getIntersection2();

                double x1 = intPnt1.getX();
                double x2 = intPnt2.getX();

                //layout from 0,0 along x pos, so greater X is the actual knee position from intersection
                //of ehe two circles
                if (x1 > x2) {
                    kneePoint.setLocation(intPnt1);
                } else {
                    kneePoint.setLocation(intPnt2);
                }

            } else if (intPnt.result() == IntersectionPointResult.ONE_POINT) {

                //only one intersection (tangents?)
                Point2D intPnt1 = intPnt.getIntersection1();
                kneePoint.setLocation(intPnt1);

            }

            double kx = kneePoint.getX();
            double ky = kneePoint.getY();

            solution.upperLeg = new Line2D.Double(hipX, hipY, kx, ky);
            solution.lowerLeg = new Line2D.Double(kx, ky, ankleBase.getX(), ankleBase.getY());

        } //end if else

        return solution;
    }

    /**
     * Internal class for passing solution values.
     */
    private class LegSolution {

        public Line2D upperLeg;
        public Line2D lowerLeg;
        public Line2D ankle;
        public Line2D foot;
        public Line2D toe;

        LegSolution() {
            upperLeg = null; 
            lowerLeg = null;
            ankle = null; 
            foot = null;
            toe = null; 
        }

    }

    /**
     * Solve the kinematics of the hip, torso, and arms
     *
     * @param sitPoint The sit point on the saddle - this is the base point.
     * @param handPoint The hand point (center of palm)
     * @return
     */
    private UpperSolution solveUpper(Point2D sitPoint, Point2D handPoint) {

        UpperSolution solution = new UpperSolution();

        double hand = _riderSize.getWristToPalm(); //_attrWristToPalm.getDoubleValue();
        double lArm = _riderSize.getLowerArm(); //_attrLowerArm.getDoubleValue();
        double uArm = _riderSize.getUpperArm(); //_attrUpperArm.getDoubleValue();
        double torso = _riderSize.getTorso(); //_attrTorso.getDoubleValue();

        Point2D shoulderPoint = new Point2D.Double();
        Point2D elbowPoint = new Point2D.Double();
        Point2D wristPoint = new Point2D.Double();
        Point2D palmPoint = new Point2D.Double();

        double torsoAngleRad = 0;

        //double armBendRad = Utilities.degreesToRadians(_attrArmBend.getDoubleValue());
        double armBendRad = Utilities.degreesToRadians(_riderPose.getArmBend()); //_armBend);
        double wristBendRad = Utilities.degreesToRadians(_riderPose.getWristBend());
//        double armRadius = (wristPalm + lArm) * Math.cos(armBendRad); //lower arm length at bend radius
//        armRadius += uArm * Math.cos(armBendRad); //upper aarm length at bend radius
        //double abHalf = armBendRad / 2.0;

        //layout along x axis for length
        Point2D apS = new Point2D.Double(0, 0);//shoulder
        Point2D apE = new Point2D.Double(uArm, 0);//elbow
        Point2D apW = Utilities.polarPoint(apE, lArm, armBendRad);//wrist
        //Point2D apH = Utilities.polarPoint(apW, hand, armBendRad);//hand
        Point2D apH = Utilities.polarPoint(apW, hand, armBendRad + wristBendRad);//hand

        double bentArmLength = apS.distance(apH);

        double uArmTheta = Utilities.anglePointPoint(apS, apH);//shoulder to hand
        double lArmTheta = Utilities.anglePointPoint(apE, apH);//elbow to hand

        //double armLBL = (hand + lArm) * Math.cos(abHalf);
        //double armUBL = uArm * Math.cos(abHalf);
        //calc length of bent arm
        //double bentArmLength = ((hand + lArm) * Math.cos(abHalf)) + (uArm * Math.cos(abHalf));
        //bentArmLength += uArm * Math.cos(abHalf);
        //total reach capability (superman pose)
        double totalLength = torso + bentArmLength;

        //sit point to hand point distance
        double sitHandDistance = sitPoint.distance(handPoint);

        if (sitHandDistance > totalLength) {
            //too far to reach...
            double theta = Utilities.anglePointPoint(sitPoint, handPoint);

            shoulderPoint = Utilities.polarPoint(sitPoint, torso, theta);
            elbowPoint = Utilities.polarPoint(shoulderPoint, uArm, theta - uArmTheta);
            wristPoint = Utilities.polarPoint(elbowPoint, lArm, theta + armBendRad - uArmTheta);
//            palmPoint = Utilities.polarPoint(wristPoint, hand, theta + armBendRad - uArmTheta);
            palmPoint = Utilities.polarPoint(wristPoint, hand, theta + armBendRad - uArmTheta + wristBendRad);

            //store for solution
            torsoAngleRad = theta;

        } else {

            //circle circle intersection
            IntersectionPoint intPnt = circleCircleIntersection(sitPoint, torso, handPoint, bentArmLength);

            if (intPnt.result() == IntersectionPointResult.TWO_POINTS) {

                Point2D ip1 = intPnt.getIntersection1();
                Point2D ip2 = intPnt.getIntersection2();
                //highest intersection point of the two circles
                if (ip1.getY() > ip2.getY()) {
                    shoulderPoint.setLocation(ip1);
                } else {
                    shoulderPoint.setLocation(ip2);
                }

            } else if (intPnt.result() == IntersectionPointResult.ONE_POINT) {

                Point2D ip1 = intPnt.getIntersection1();
                shoulderPoint.setLocation(ip1);
            }

            double theta = Utilities.anglePointPoint(shoulderPoint, handPoint);
            //store for solution
            torsoAngleRad = Utilities.anglePointPoint(sitPoint, shoulderPoint);

            //project polar coords for joints
            elbowPoint = Utilities.polarPoint(shoulderPoint, uArm, theta - uArmTheta);
            wristPoint = Utilities.polarPoint(elbowPoint, lArm, theta + armBendRad - uArmTheta);
//            palmPoint = Utilities.polarPoint(wristPoint, hand, theta + armBendRad - uArmTheta);
            palmPoint = Utilities.polarPoint(wristPoint, hand, theta + armBendRad - uArmTheta + wristBendRad);

        }//end if/else too short

        solution.torso = new Line2D.Double(sitPoint, shoulderPoint);
        solution.upperArm = new Line2D.Double(shoulderPoint, elbowPoint);
        solution.lowerArm = new Line2D.Double(elbowPoint, wristPoint);
        solution.hand = new Line2D.Double(wristPoint, palmPoint);

        //torso angle
        solution.torsoAngleRad = torsoAngleRad;

        return solution;

    }

    private class UpperSolution {

        public Line2D torso;
        public Line2D upperArm;
        public Line2D lowerArm;
        public Line2D hand;

        public double torsoAngleRad;

    }

    /**
     * Update the Bicycle report with the rider geometry
     *
     * @param report Report to record values in.
     */
    public void updateReport(Report report) {

        //torso
        double torsoTheta = Utilities.anglePointPoint(_geomTorso.getP1(), _geomTorso.getP2());
        report.reportAngle(ReportField.TorsoH, torsoTheta);

        //upper arm angle
        double uArmTheta = Utilities.vectorAngle(_geomTorso.getP2(), _geomTorso.getP1(), _geomUpperArm.getP2());
        report.reportAngle(ReportField.TorsoUA, uArmTheta);

        //lower arm angle / elbow
        double lArmTheta = Utilities.vectorAngle(_geomUpperArm.getP2(), _geomUpperArm.getP1(), _geomLowerArm.getP2());
        report.reportAngle(ReportField.Elbow, lArmTheta);

        //=========   Right Leg ============
        //right thigh in horizontal
        double rightThighAngle = Utilities.vectorAngle(_geomUpperLegRight.getP1(), _geomUpperLegRight.getP2(), _geomTorso.getP2());
        report.reportAngle(ReportField.RThighTorso, rightThighAngle);

        //right knee angle
        double rightLegKneeAngle = Utilities.vectorAngle(_geomUpperLegRight.getP2(), _geomUpperLegRight.getP1(), _geomLowerLegRight.getP2());
        report.reportAngle(ReportField.RKnee, rightLegKneeAngle);

        //knee to pedal  (ball of foot / toe start)
        Point2D rkPoint = _geomUpperLegRight.getP2();
        Point2D rbtPoint = _geomToeRight.getP1();
        double rdeltaX = rkPoint.getX() - rbtPoint.getX();
        report.reportDistance(ReportField.RKneePedal, rdeltaX);

        //=========   Left Leg ============
        //right thigh in horizontal
        double leftThighAngle = Utilities.vectorAngle(_geomUpperLegLeft.getP1(), _geomUpperLegLeft.getP2(), _geomTorso.getP2());
        report.reportAngle(ReportField.LThighTorso, leftThighAngle);

        //left knee angle
        double leftLegKneeAngle = Utilities.vectorAngle(_geomUpperLegLeft.getP2(), _geomUpperLegLeft.getP1(), _geomLowerLegLeft.getP2());
        report.reportAngle(ReportField.LKnee, leftLegKneeAngle);

        //knee to pedal (ball of foot / toe start)
        Point2D lkPoint = _geomUpperLegLeft.getP2();
        Point2D lbtPoint = _geomToeLeft.getP1();
        double ldeltaX = lkPoint.getX() - lbtPoint.getX();
        report.reportDistance(ReportField.LKneePedal, ldeltaX);

    }

    /**
     * Render the left hand side of the rider.
     * @param g2  The graphics object to render to.
     * @param color The color of the rider.
     */
    public void renderLeft(Graphics2D g2, Color color) {

        g2.setPaint(color);
        g2.setStroke(new BasicStroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        g2.draw(_geomUpperLegLeft);
        g2.draw(_geomLowerLegLeft);
        g2.draw(_geomAnkleLeft);
        g2.draw(_geomFootLeft);
        g2.draw(_geomToeLeft);

    }

    /**
     * Render the right hand side of the rider.
     * @param g2  The graphics object to render to.
     * @param color The color of the rider.
     */    
    public void renderRight(Graphics2D g2, Color color) {

        g2.setPaint(color);
        g2.setStroke(new BasicStroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

        g2.draw(_geomTorso);
        g2.draw(_geomUpperArm);
        g2.draw(_geomLowerArm);
        g2.draw(_geomHand);

        g2.draw(_geomUpperLegRight);
        g2.draw(_geomLowerLegRight);
        g2.draw(_geomAnkleRight);
        g2.draw(_geomFootRight);
        g2.draw(_geomToeRight);

 
        g2.draw(_geomHead);

       
        
    }

    
}//end class
