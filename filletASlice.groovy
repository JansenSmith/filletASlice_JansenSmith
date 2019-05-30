

double rad =2

def corner(double rad, double angle){
	return CSG.unionAll(Extrude.revolve(new Fillet(rad,0.01).toCSG().rotz(-90), 0,  angle,  4))
				.difference(Extrude.revolve(new Sphere(rad).toCSG().toYMin().toZMin(), 0,  angle,  4))
				//.rotz(180)
}


def outerFillet(List<Polygon> polys, double rad){
	
	def parts =[]
	for(Polygon p:polys){
		int size = p.vertices.size()
		for (int i=0;i<size;i++){
			//if(i>1)
			//	continue;
			int next = i+1;
			if(next==size)
				next=0;
			int nextNext = next+1;
			if(nextNext==size)
				nextNext=0;
			Vector3d position0 =p.vertices.get(i).pos  
			Vector3d position1 =p.vertices.get(next).pos 
			Vector3d position2 =p.vertices.get(nextNext).pos 
			Vector3d seg1 = position0.minus(position1)
			Vector3d seg2  = position2.minus(position1)
			double len = seg1.magnitude()
			double angle =Math.toDegrees(seg1.angle(seg2))
			double angleAbs =Math.toDegrees(seg1.angle(Vector3d.Y_ONE))
			CSG fillet = new Fillet(rad,len).toCSG()
						.toYMax()
						//.roty(90)
			if(seg1.x<0){
				angleAbs=360-angleAbs
				//fillet=fillet.toYMax()
			}
			if(Math.abs(angle)>0.01 && Math.abs(angle)<180){
				parts.add(corner( rad,  angle)
					.rotz(angleAbs)
				.move(position0))
			}
			println "Fillet corner Angle = "+angle
			parts.add(fillet
					.rotz(angleAbs)
				.move(position0))
		}
	}
	return parts
}

def base = new Cube(20).toCSG()
			.difference(new Cube(5,10,20).toCSG())
			.difference(new Cube(10,5,20).toCSG())
			.rotz(5)
			.toZMin()
List<Polygon> polys = Slice.slice(base)
return [outerFillet( polys,rad),polys]