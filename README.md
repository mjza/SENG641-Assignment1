# SENG641-Assignment1
Assignment #1 of SENG641  

- Add a button to the toolbar that allows the user to draw some particular shape not currently provided explicitly.

- Add buttons to the toolbar that permit the user to shift a figure one step forward or one step backwards in terms of the order in which they are drawn atop each other. Note that this is similar though not identical to the Bring To Front and Send To Back commands available in the Edit menu.
- "Smart" connection lines can be drawn between figures such that, when the figures are resized or moved, the connection lines are maintained between the figures. For many figures these connection lines are computed to lie on the line between the centres of the two objects; the line segment that gets drawn is that portion of this line that lies "between" the two figures (although the definition of "between" can seem a bit funny if the figures overlap). An alternative would be to determine the edge of Figure A that is closest to Figure B and the edge of Figure B that is closest to Figure A, and then to draw the line segment between the midpoints of these two edges. (Note that you can consider a curve to consist of lots of very small line segments.)
Modify the provided connection lines to use this alternative.
- Extend JHotDraw so that it can be used in 3-D drawing applications.
